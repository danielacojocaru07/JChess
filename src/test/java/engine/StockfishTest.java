package engine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;

class StockfishTest {

    private Stockfish stockfish;

    @BeforeEach
    void setUp() {
        stockfish = new Stockfish();
    }

    @Test
    void testStartEngine() {
        boolean started = stockfish.startEngine();
        assertTrue(started, "Stockfish engine should start successfully.");
    }

    @Test
    void testSendCommand() throws IOException {
        stockfish.startEngine();
        String command = "uci";
        stockfish.sendCommand(command);

        // We cannot directly verify output without mocks, so we will attempt to get output from Stockfish
        String output = stockfish.getOutput(1000);
        assertTrue(output.contains("uciok"), "Stockfish should respond with 'uciok' after 'uci' command.");
    }

    @Test
    void testGetOutput() throws IOException {
        stockfish.startEngine();

        // Send a simple command and get output
        stockfish.sendCommand("uci");
        String output = stockfish.getOutput(1000);

        assertTrue(output.contains("uciok"), "Output should contain 'uciok' after sending 'uci' command.");
    }

    @Test
    void testStopEngine() {
        stockfish.startEngine();
        stockfish.stopEngine();

        // Since we cannot verify process termination directly, we will assume no exception occurred.
        assertDoesNotThrow(() -> stockfish.stopEngine(), "Engine stop should not throw exceptions.");
    }

    @Test
    void testGetBestMove() throws IOException {
        String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"; // Example FEN
        int depth = 3;

        stockfish.startEngine();
        String bestMove = stockfish.getBestMove(fen, depth);

        assertNotNull(bestMove, "Stockfish should return a valid best move.");
        assertTrue(bestMove.matches("[a-h][1-8][a-h][1-8]"), "The best move should be in UCI format.");
    }

    @Test
    void testExtractStockfishExe() throws IOException {
        // Test if the extraction of the Stockfish executable works properly
        File tempFile = stockfish.extractStockfishExe();

        assertNotNull(tempFile, "The temporary file for Stockfish executable should be created.");
        assertTrue(tempFile.exists(), "The temporary file should exist.");
        assertTrue(tempFile.getName().contains("stockfish"), "The file name should contain 'stockfish'.");
    }

    @Test
    void testStartEngineFailure() {
        // Simulate the failure of extracting the Stockfish executable by deleting it from resources
        stockfish = new Stockfish() {
            @Override
            protected File extractStockfishExe() throws IOException {
                throw new IOException("File not found");
            }
        };

        boolean started = stockfish.startEngine();
        assertFalse(started, "Stockfish engine should fail to start if executable is not found.");
    }
}