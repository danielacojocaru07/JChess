package engine;

import java.io.*;
import java.nio.file.*;

/// @author dana

/**
 * This class interacts with the Stockfish chess engine to make moves, get engine output, and manage its lifecycle.
 */
public class Stockfish {
    /// Process instance for the running Stockfish engine.
    private Process stockfishProcess;
    /// Buffered reader for reading output from the Stockfish engine.
    private BufferedReader reader;
    /// Buffered writer for sending commands to the Stockfish engine.
    private BufferedWriter writer;

    /**
     * Extracts the Stockfish executable from the resources and saves it as a temporary file.
     *
     * @return The temporary file containing the Stockfish executable.
     * @throws IOException If an error occurs while reading the executable or writing the temporary file.
     */
    File extractStockfishExe() throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("engine/stockfish.exe");

        if (inputStream == null) {
            throw new FileNotFoundException("Stockfish executable not found in resources.");
        }

        // Create a temporary file for stockfish.exe
        Path tempPath = Files.createTempFile("stockfish", ".exe");
        tempPath.toFile().deleteOnExit(); // Ensure it gets deleted on exit

        try (FileOutputStream outputStream = new FileOutputStream(tempPath.toFile())) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        }

        return tempPath.toFile();
    }

    /**
     * Starts the Stockfish engine by extracting the executable and launching a process.
     *
     * @return True if the engine started successfully, false otherwise.
     */
    public boolean startEngine() {
        try {
            File stockfishExe = extractStockfishExe();
            stockfishProcess = new ProcessBuilder(stockfishExe.getAbsolutePath()).start();
            reader = new BufferedReader(new InputStreamReader(stockfishProcess.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(stockfishProcess.getOutputStream()));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Sends a command to the Stockfish engine.
     *
     * @param command The command to send to the engine.
     * @throws IOException If an error occurs while sending the command.
     */
    public void sendCommand(String command) throws IOException {
        writer.write(command + "\n");
        writer.flush();
    }

    /**
     * Reads the output from the Stockfish engine for a specified amount of time.
     *
     * @param timeoutMillis The maximum time (in milliseconds) to wait for output.
     * @return The engine's output as a string.
     * @throws IOException If an error occurs while reading the output.
     */
    public String getOutput(int timeoutMillis) throws IOException {
        StringBuilder output = new StringBuilder();
        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startTime < timeoutMillis) {
            if (reader.ready()) {
                output.append(reader.readLine()).append("\n");
            }
        }

        return output.toString();
    }

    /**
     * Stops the Stockfish engine by sending the quit command and destroying the process.
     */
    public void stopEngine() {
        try {
            sendCommand("quit");
            stockfishProcess.destroy();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the best move from the Stockfish engine for a given board position and search depth.
     *
     * @param fen The FEN (Forsyth-Edwards Notation) string representing the board position.
     * @param depth The search depth for Stockfish to analyze.
     * @return The best move in UCI format, or null if no move is found.
     * @throws IOException If an error occurs while interacting with the engine.
     */
    public String getBestMove(String fen, int depth) throws IOException {
        sendCommand("position fen " + fen);
        sendCommand("go depth " + depth);
        String output = getOutput(1000);

        for (String line : output.split("\n")) {
            if (line.startsWith("bestmove")) {
                return line.split(" ")[1]; // Extract the best move
            }
        }
        return null;
    }
}