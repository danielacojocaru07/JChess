package gui;

import engine.Stockfish;
import pieces.*;
import elements.*;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.awt.Color.*;

/// @author dana

/**
 * The App class serves as the main entry point and manager for the Chess game application.
 * It handles user interactions, game state management, and database operations.
 * <p>
 * Main responsibilities include:
 * - Initializing the graphical user interface (GUI)
 * - Communicating with the Stockfish chess engine
 * - Managing database records for games, moves, and positions
 * - Handling game logic such as player moves and captured pieces
 */
public class App {
    /// The currently active game instance.
    public static Game game;
    /// The unique identifier for the current game in the database.
    private static int gameID;
    /// The unique identifier for the current board position in the database.
    private static int positionID;
    /// Flag indicating if it is the user's turn.
    private static boolean isUserTurn;
    /// Flag indicating if the chess engine has made a move.
    private static boolean engineMoved;
    /// The main game frame displayed to the user.
    public static JFrame gameFrame;
    /// The chessboard panel containing the squares and pieces.
    private static JPanel board;
    /// A 2D array of buttons representing the chessboard squares.
    private static JButton[][] squareButtons;
    /// The currently selected square on the board.
    private static Square selectedSquare = null;

    // Constants for chess piece icons
    private static final ImageIcon WHITE_PAWN = loadIcon("pieces/white-pawn.png");
    private static final ImageIcon BLACK_PAWN = loadIcon("pieces/black-pawn.png");
    private static final ImageIcon WHITE_ROOK = loadIcon("pieces/white-rook.png");
    private static final ImageIcon BLACK_ROOK = loadIcon("pieces/black-rook.png");
    private static final ImageIcon WHITE_KNIGHT = loadIcon("pieces/white-knight.png");
    private static final ImageIcon BLACK_KNIGHT = loadIcon("pieces/black-knight.png");
    private static final ImageIcon WHITE_BISHOP = loadIcon("pieces/white-bishop.png");
    private static final ImageIcon BLACK_BISHOP = loadIcon("pieces/black-bishop.png");
    private static final ImageIcon WHITE_QUEEN = loadIcon("pieces/white-queen.png");
    private static final ImageIcon BLACK_QUEEN = loadIcon("pieces/black-queen.png");
    private static final ImageIcon WHITE_KING = loadIcon("pieces/white-king.png");
    private static final ImageIcon BLACK_KING = loadIcon("pieces/black-king.png");

    /// Size of the chessboard (8x8).
    private static final int BOARD_SIZE = 8;

    /**
     * Entry point of the application. Initializes the main menu.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(App::startMenu);
    }

    /**
     * Initiates the Stockfish engine to calculate the best move and performs the move.
     */
    public static void stockfishMove() {
        String FEN;
        if (game.gameHistory.isEmpty()) {
            FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        } else {
            FEN = game.FENNotation();
        }

        // Run the engine in a separate thread
        new Thread(() -> {
            String bestMove = getStockfishMove(FEN);

            if (!bestMove.isEmpty()) {
                String startSquare = bestMove.substring(0, 2);
                String endSquare = bestMove.substring(2, 4);

                int startRow = Integer.parseInt(startSquare.substring(1)) - 1;
                int startCol = startSquare.charAt(0) - 'a';
                int endRow = Integer.parseInt(endSquare.substring(1)) - 1;
                int endCol = endSquare.charAt(0) - 'a';

                // Update UI components on the Event Dispatch Thread
                SwingUtilities.invokeLater(() -> {
                    System.out.println("Clicking start square: " + startRow + "," + startCol);
                    squareButtons[startRow][startCol].doClick();  // Simulate click on start square
                    System.out.println("Clicking end square: " + endRow + "," + endCol);
                    squareButtons[endRow][endCol].doClick();  // Simulate click on end square
                });
            } else {
                System.out.println("Failed to get Stockfish move.");
            }
        }).start();
    }

    /**
     * Retrieves the best move from the Stockfish engine given a position in FEN format.
     *
     * @param FEN The FEN representation of the current board position.
     * @return The best move calculated by Stockfish.
     */
    public static String getStockfishMove(String FEN) {
        Stockfish engine = new Stockfish();
        String bestMove = "";
        if (engine.startEngine()) {
            try {
                bestMove = engine.getBestMove(FEN, 10);  // Timeout set to 10 seconds
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                engine.stopEngine();
            }
        } else {
            System.out.println("Failed to start Stockfish engine.");
        }
        return bestMove;
    }

    /**
     * Inserts a new game record into the database with the specified player's color.
     *
     * @param myColor The color the player has chosen ("White" or "Black").
     */
    public static void insertNewGame(String myColor) {
        if (!myColor.equalsIgnoreCase("White") && !myColor.equalsIgnoreCase("Black")) {
            System.out.println("Invalid color. Please use 'White' or 'Black'.");
            return;
        }

        String jdbcURL = "jdbc:derby:D:/University/Programming/JChess/database;create=true";
        String insertSQL = "INSERT INTO Games (MyColor, Status) VALUES (?, ?)";
        String lastIDSQL = "SELECT MAX(GameID) AS LastID FROM Games";

        try (Connection connection = DriverManager.getConnection(jdbcURL);
             PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {

            // Set the parameters for the prepared statement
            preparedStatement.setString(1, myColor); // Player's color
            preparedStatement.setString(2, "Not Finished"); // Default game status

            // Execute the insert statement
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                try (Statement lastIDStmt = connection.createStatement();
                     ResultSet rs = lastIDStmt.executeQuery(lastIDSQL)) {

                    if (rs.next()) {
                        gameID = rs.getInt("LastID");
                        System.out.println("New game inserted successfully! GameID: " + gameID);
                    }
                }
            } else {
                System.out.println("Failed to insert a new game.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Database connection or query execution failed.");
        }
    }

    /**
     * Updates the game record in the database when the game ends.
     *
     * @param myColor The color of the player.
     * @param result  The result of the game ("White", "Black", or "Draw").
     */
    public static void editEndGame(String myColor, String result) {
        if (!myColor.equalsIgnoreCase("White") && !myColor.equalsIgnoreCase("Black")) {
            System.out.println("Invalid color. Please use 'White' or 'Black'.");
            return;
        }
        if (!result.equalsIgnoreCase("White") && !result.equalsIgnoreCase("Black") && !result.equalsIgnoreCase("Draw")) {
            System.out.println("Invalid result. Please use 'White', 'Black', or 'Draw'.");
            return;
        }

        String jdbcURL = "jdbc:derby:database;create=true";
        String updateSQL = "UPDATE Games SET EndTimestamp = ?, Status = ?, Result = ? " +
                "WHERE GameID = (SELECT GameID FROM Games WHERE MyColor = ? AND Status = 'Not Finished' " +
                "ORDER BY StartTimestamp DESC FETCH FIRST ROW ONLY)";

        try (Connection connection = DriverManager.getConnection(jdbcURL);
             PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)) {

            // Format the current timestamp for the end time
            String endTimestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            // Set parameters for the prepared statement
            preparedStatement.setTimestamp(1, Timestamp.valueOf(endTimestamp)); // End timestamp
            preparedStatement.setString(2, "Finished"); // Status
            preparedStatement.setString(3, result); // Result
            preparedStatement.setString(4, myColor); // Player's color

            // Execute the update statement
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Game updated successfully!");
            } else {
                System.out.println("No unfinished game found for the given color.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Database connection or query execution failed.");
        }
    }

    /**
     * Inserts a new move record into the database.
     *
     * @param moveNumber   The move number in the game.
     * @param playerTurn   The player who made the move ("White" or "Black").
     * @param moveNotation The move in chess notation.
     */
    public static void insertNewMove(int moveNumber, String playerTurn, String moveNotation) {
        String jdbcURL = "jdbc:derby:D:/University/Programming/JChess/database;create=true";
        String insertSQL = "INSERT INTO Moves (GameID, PositionID, MoveNumber, PlayerTurn, MoveNotation, Timestamp) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(jdbcURL);
             PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {

            // Set the parameters for the prepared statement
            preparedStatement.setInt(1, gameID);
            preparedStatement.setInt(2, positionID);
            preparedStatement.setInt(3, moveNumber);
            preparedStatement.setString(4, playerTurn);
            preparedStatement.setString(5, moveNotation);
            preparedStatement.setTimestamp(6, new Timestamp(System.currentTimeMillis())); // Set current timestamp // Default game status

            // Execute the insert statement
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Move inserted successfully!");
            } else {
                System.out.println("Failed to insert the move.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Database connection or query execution failed.");
        }
    }

    /**
     * Inserts player details into the database for the current game.
     *
     * @param myColor       The player's color.
     * @param opponentColor The opponent's color.
     */
    public static void insertPlayers(String myColor, String opponentColor) {
        String jdbcURL = "jdbc:derby:D:/University/Programming/JChess/database;create=true";
        String insertSQL = "INSERT INTO Players (GameID, MyColor, OpponentColor, MyCapturedPieces, OpponentCapturedPieces) "
                + "VALUES (?, ?, ?, '', '')";

        try (Connection connection = DriverManager.getConnection(jdbcURL);
             PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {

            // Set the parameters for the prepared statement
            preparedStatement.setInt(1, gameID);                 // GameID
            preparedStatement.setString(2, myColor);            // MyColor
            preparedStatement.setString(3, opponentColor);      // OpponentColor

            // Execute the insert statement
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Player record inserted successfully!");
            } else {
                System.out.println("Failed to insert the player record.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Database connection or query execution failed.");
        }
    }

    /**
     * Updates the record of captured pieces for a player in the database.
     *
     * @param isMyCapture  Whether the piece was captured by the player.
     * @param capturedPiece The captured piece.
     */
    public static void editCapturedPieces(boolean isMyCapture, String capturedPiece) {
        String jdbcURL = "jdbc:derby:D:/University/Programming/JChess/database;create=true";
        String updateSQL = isMyCapture
                ? "UPDATE Players SET MyCapturedPieces = MyCapturedPieces || ? WHERE GameID = ?"
                : "UPDATE Players SET OpponentCapturedPieces = OpponentCapturedPieces || ? WHERE GameID = ?";

        try (Connection connection = DriverManager.getConnection(jdbcURL);
             PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)) {

            // Set the parameters for the prepared statement
            preparedStatement.setString(1, capturedPiece); // Append the captured piece
            preparedStatement.setInt(2, gameID);           // Identify the row using GameID

            // Execute the update statement
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Captured piece updated successfully!");
            } else {
                System.out.println("Failed to update captured piece.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Database connection or query execution failed.");
        }
    }

    /**
     * Inserts a new board position record into the database.
     *
     * @param positionNumber The position number in the game.
     * @param fenString      The FEN representation of the position.
     */
    public static void insertNewPosition(int positionNumber, String fenString) {
        String jdbcURL = "jdbc:derby:D:/University/Programming/JChess/database;create=true";
        String insertSQL = "INSERT INTO Positions (GameID, PositionNumber, FENString) "
                + "VALUES (?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(jdbcURL);
             PreparedStatement preparedStatement = connection.prepareStatement(insertSQL,
                     PreparedStatement.RETURN_GENERATED_KEYS)) {

            // Set the parameters for the prepared statement
            preparedStatement.setInt(1, gameID);            // GameID
            preparedStatement.setInt(2, positionNumber);    // PositionNumber
            preparedStatement.setString(3, fenString);      // FENString

            // Execute the insert statement
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Position inserted successfully!");

                // Retrieve the auto-generated PositionID
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        positionID = generatedKeys.getInt(1); // Get the generated PositionID
                    }
                }
            } else {
                System.out.println("Failed to insert the position.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Database connection or query execution failed.");
        }
    }

    /**
     * Displays the main menu of the application.
     */
    public static void startMenu() {
        // Create the main menu frame
        JFrame mainFrame = new JFrame("Chess Game");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        mainFrame.setLayout(new GridLayout(3, 1));

        JLabel welcomeLabel = new JLabel("Welcome to ChessGame!", JLabel.CENTER);
        JButton startGameButton = new JButton("Start Game");
        JButton exitButton = new JButton("Exit");

        mainFrame.add(welcomeLabel);
        mainFrame.add(startGameButton);
        mainFrame.add(exitButton);

        // Add action listener for "Start Game"
        startGameButton.addActionListener(e -> {
            mainFrame.dispose();
            selectColorMenu();
        });

        // Add action listener for "Exit"
        exitButton.addActionListener(e -> System.exit(0));

        mainFrame.setVisible(true);
    }

    /**
     * Displays the color selection menu for the player.
     */
    public static void selectColorMenu() {
        JFrame colorFrame = new JFrame("Select Color");
        colorFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        colorFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        colorFrame.setLayout(new GridLayout(4, 1));

        JLabel colorLabel = new JLabel("Select the color you want to play!", JLabel.CENTER);
        JButton whiteButton = new JButton("White");
        JButton blackButton = new JButton("Black");
        JButton randomButton = new JButton("Random");

        colorFrame.add(colorLabel);
        colorFrame.add(whiteButton);
        colorFrame.add(blackButton);
        colorFrame.add(randomButton);

        whiteButton.addActionListener(e -> {
            insertNewGame("White");
            insertPlayers("White", "Black");
            startGame(Player.ColorOption.WHITE, colorFrame);
        });
        blackButton.addActionListener(e -> {
            insertNewGame("Black");
            insertPlayers("Black", "White");
            startGame(Player.ColorOption.BLACK, colorFrame);
        });
        randomButton.addActionListener(e -> {
            startGame(Player.ColorOption.RANDOM, colorFrame);
            String myColor = game.getMe().playerColor == Game.Color.WHITE ? "White" : "Black";
            String opponentColor = game.getMe().playerColor == Game.Color.WHITE ? "Black" : "White";
            insertNewGame(myColor);
            insertPlayers(myColor, opponentColor);
        });

        colorFrame.setVisible(true);
    }

    /**
     * Initializes and displays the main game frame.
     */
    public static void gameFrame() {
        JFrame gameFrame = new JFrame("Chess Game");
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        gameFrame.setLayout(new BorderLayout());

        // Create the chessboard panel
        JPanel boardPanel = new JPanel(new GridLayout(BOARD_SIZE, BOARD_SIZE));
        squareButtons = new JButton[BOARD_SIZE][BOARD_SIZE];

        // Initialize the board with squares
        if (game.getMe().playerColor == Game.Color.WHITE) {
            for (int i = 7; i >= 0; i--) for (int j = 0; j < 8; j++) addSquare(i, j, squareButtons, boardPanel);
        } else {
            for (int i = 0; i < 8; i++) for (int j = 7; j >= 0; j--) addSquare(i, j, squareButtons, boardPanel);
        }

        // Wrap the chessboard panel in another panel to control resizing
        JPanel squareWrapperPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                // Resize the chessboard to maintain square shape
                int size = Math.min(getWidth(), getHeight());
                boardPanel.setBounds((getWidth() - size) / 2, (getHeight() - size) / 2, size, size);

                // Ensure buttons are properly resized after layout
                SwingUtilities.invokeLater(() -> {
                    // Initialize the board with pieces
                    if (game.getMe().playerColor == Game.Color.WHITE) {
                        for (int i = 7; i >= 0; i--) for (int j = 0; j < 8; j++) addPiece(i, j, squareButtons);
                    } else {
                        for (int i = 0; i < 8; i++) for (int j = 7; j >= 0; j--) addPiece(i, j, squareButtons);
                    }
                });
            }
        };
        squareWrapperPanel.add(boardPanel);

        board = squareWrapperPanel;

        // Add a control panel for additional actions
        JPanel controlPanel = new JPanel(new FlowLayout());
        JButton restartButton = new JButton("Restart");
        JButton exitButton = new JButton("Exit");

        // Add action listeners
        restartButton.addActionListener(e -> {
            gameFrame.dispose();
            App.selectColorMenu();
        });
        exitButton.addActionListener(e -> System.exit(0));

        controlPanel.add(restartButton);
        controlPanel.add(exitButton);

        // Add components to the frame
        gameFrame.add(board, BorderLayout.CENTER);
        gameFrame.add(controlPanel, BorderLayout.SOUTH);

        // Display the frame
        gameFrame.setVisible(true);
        update(board, squareButtons);
    }

    /**
     * Starts a new game by disposing of the color selection frame, initializing a game with the chosen color,
     * and opening the game frame. Also determines if it's the user's turn based on the chosen color.
     *
     * @param colorOption the selected color for the player.
     * @param colorFrame  the JFrame used for color selection.
     */
    public static void startGame(Player.ColorOption colorOption, JFrame colorFrame) {
        colorFrame.dispose();
        game = new Game(colorOption);
        gameFrame();
        isUserTurn = game.getMe().playerColor == Game.Color.WHITE;
    }

    /**
     * Displays a dialog showing the game result and provides options to restart or exit the game.
     *
     * @param result the result of the game (e.g., "White wins", "Black wins", or "Draw").
     */
    public static void endGame(String result) {
        // Create a dialog to display the game result
        JFrame endGameFrame = new JFrame("Game Over");
        endGameFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        endGameFrame.setSize(300, 200);
        endGameFrame.setLayout(new BorderLayout());

        // Label to display the result
        JLabel resultLabel = new JLabel(result, JLabel.CENTER);
        resultLabel.setFont(new Font("Arial", Font.BOLD, 20));
        endGameFrame.add(resultLabel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());

        // Restart button
        JButton restartButton = new JButton("Restart");
        restartButton.addActionListener(e -> {
            if (gameFrame != null) {
                gameFrame.dispose();
            }
            endGameFrame.dispose();
            selectColorMenu();
        });

        // Exit button
        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> System.exit(0));

        // Add buttons to the panel
        buttonPanel.add(restartButton);
        buttonPanel.add(exitButton);

        // Add button panel to the frame
        endGameFrame.add(buttonPanel, BorderLayout.SOUTH);

        // Center and display the dialog
        endGameFrame.setLocationRelativeTo(null);
        endGameFrame.setVisible(true);
    }

    /**
     * Updates the board UI to reflect the current game state, including pieces, highlights, and game status.
     * Also handles the transition between the user's and the engine's turns.
     *
     * @param boardPanel    the JPanel representing the chessboard.
     * @param squareButtons the 2D array of buttons representing the board squares.
     */
    private static void update(JPanel boardPanel, JButton[][] squareButtons) {
        // Clear and refresh each square
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (game.currentPosition.board[i][j] == selectedSquare) {
                    // Highlight the selected square
                    squareButtons[i][j].setBackground(LIGHT_GRAY);
                } else {
                    // Reset the square's background color
                    squareButtons[i][j].setBackground(
                            game.currentPosition.board[i][j].getColor() == Game.Color.WHITE ? WHITE : GRAY
                    );
                }

                // Update the piece icon
                addPiece(i, j, squareButtons);
                if (game.currentPosition.board[i][j].getIsEmpty()) {
                    squareButtons[i][j].setIcon(null);
                }
            }
        }

        // Check if the king is in check
        Square kingSquare = findKingSquare(game.whiteMoves ? Game.Color.WHITE : Game.Color.BLACK);
        if (kingSquare != null && (game.isCheckForWhite() || game.isCheckForBlack())) {
            int kingRank = kingSquare.getRank() - 1;
            int kingFile = kingSquare.getFile() - 'a';
            squareButtons[kingRank][kingFile].setBackground(RED);
        }

        // Check the status of the game
        if (game.isCheckmateForWhite()) {
            editEndGame(game.getMe().playerColor == Game.Color.WHITE ? "White" : "Black", "Black");
            endGame("Black wins");
        } else if (game.isCheckmateForBlack()) {
            editEndGame(game.getMe().playerColor == Game.Color.WHITE ? "White" : "Black", "White");
            endGame("White wins");
        } else if (game.isDraw()) {
            editEndGame(game.getMe().playerColor == Game.Color.WHITE ? "White" : "Black", "Draw");
            endGame("Draw");
        }

        // Repaint the board to reflect changes
        boardPanel.revalidate();
        boardPanel.repaint();

        isUserTurn = (game.getMe().playerColor == Game.Color.WHITE && game.whiteMoves)
                || game.getMe().playerColor == Game.Color.BLACK && !game.whiteMoves;

        if (isUserTurn) engineMoved = false;

        // Alternate turns based on current player's color and move
        if (!isUserTurn && !engineMoved) {
            stockfishMove();
            isUserTurn = true;
            engineMoved = true;
        }
    }

    /**
     * Finds the square containing the king of the specified color on the current board position.
     *
     * @param kingColor the color of the king to find.
     * @return the Square containing the king, or null if the king is not found.
     */
    private static Square findKingSquare(Game.Color kingColor) {
        Position position = game.currentPosition;

        // Iterate through the board to find the king
        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                Piece piece = position.board[rank][file].getPiece();
                if (piece instanceof King && piece.color == kingColor) {
                    return position.board[rank][file];
                }
            }
        }
        return null;
    }

    /**
     * Adds a square to the chessboard with appropriate background color and action listeners.
     *
     * @param i               the row index of the square.
     * @param j               the column index of the square.
     * @param squareButtons   the 2D array of buttons representing the board squares.
     * @param chessBoardPanel the JPanel representing the chessboard.
     */
    private static void addSquare(int i, int j, JButton[][] squareButtons, JPanel chessBoardPanel) {
        JButton squareButton = new JButton();
        squareButton.setOpaque(true);
        squareButton.setBorderPainted(false);

        squareButton.setBackground(game.currentPosition.board[i][j].getColor() == Game.Color.WHITE ? WHITE : GRAY);

        // Add an action listener to handle clicks
        squareButton.addActionListener(e -> handleSquareClick(i, j));

        squareButtons[i][j] = squareButton;
        chessBoardPanel.add(squareButton);
    }

    /**
     * Adds a chess piece icon to a board square button, resizing the icon to fit the button.
     *
     * @param i               the row index of the square.
     * @param j               the column index of the square.
     * @param squareButtons   the 2D array of buttons representing the board squares.
     */
    private static void addPiece(int i, int j, JButton[][] squareButtons) {
        JButton squareButton = squareButtons[i][j];
        int buttonWidth = squareButton.getWidth();
        int buttonHeight = squareButton.getHeight();
        Square square = App.game.currentPosition.board[i][j];
        if (!square.getIsEmpty() && buttonWidth > 0 && buttonHeight > 0) {
            squareButton.setIcon(scaleIcon(getPieceIcon(square.getPiece()), squareButton));
        }
    }

    /**
     * Handles user interaction when a board square is clicked, including selecting pieces and making moves or captures.
     *
     * @param row the row index of the clicked square.
     * @param col the column index of the clicked square.
     */
    private static void handleSquareClick(int row, int col) {
        Square clickedSquare = game.currentPosition.board[row][col];

        if (selectedSquare == null) {
            handleSelection(clickedSquare);
        } else {
            handleAction(clickedSquare);
            selectedSquare = null; // Reset selection after the action
        }
        update(board, squareButtons);
    }

    /**
     * Handles the selection of a square by the user, ensuring the selection is valid based on the game rules.
     *
     * @param clickedSquare the square selected by the user.
     */
    private static void handleSelection(Square clickedSquare) {
        if (!clickedSquare.getIsEmpty()) {
            Piece piece = clickedSquare.getPiece();

            // Ensure it's the correct turn and piece color
            if ((game.whiteMoves && piece.color == Game.Color.WHITE) || (!game.whiteMoves && piece.color == Game.Color.BLACK)) {
                selectedSquare = clickedSquare;
                System.out.println("Selected piece: " + piece);
            } else {
                System.out.println("It's not your turn or invalid piece selection.");
            }
        }
    }

    /**
     * Handles the action when a previously selected piece interacts with a target square (move or capture).
     *
     * @param clickedSquare the target square of the action.
     */
    private static void handleAction(Square clickedSquare) {
        if (clickedSquare.getIsEmpty()) {
            handleMove(clickedSquare);
        } else {
            handleCapture(clickedSquare);
        }
    }

    /**
     * Handles a piece move to an empty square, validating and applying the move if it is allowed.
     *
     * @param clickedSquare the target empty square for the move.
     */
    private static void handleMove(Square clickedSquare) {
        Piece selectedPiece = selectedSquare.getPiece();

        if (selectedPiece.allowedMove(selectedSquare, clickedSquare, game)) {
            selectedPiece.makeMove(selectedSquare, clickedSquare, game);

            Move lastMove = game.gameHistory.get(game.gameHistory.size() - 1);
            System.out.println("Moved piece " + lastMove.getMovedPiece() +
                    " from " + lastMove.getStart() + " to " + lastMove.getEnd());

            insertNewPosition(lastMove.getPositionAfterMove().positionNumber, game.FENNotation());
            insertNewMove(lastMove.getMoveNumber(), !game.whiteMoves ? "White" : "Black", lastMove.getMoveNotation());
            game.display();
        } else if (selectedPiece instanceof Pawn && selectedPiece.allowedCapture(selectedSquare, clickedSquare, game)) {
            selectedPiece.makeCapture(selectedSquare, clickedSquare, game);

            Move thisMove = game.gameHistory.get(game.gameHistory.size() - 1);
            Piece capturingPiece = thisMove.getMovedPiece();

            Move lastMove = game.gameHistory.get(game.gameHistory.size() - 2);
            Piece capturedPiece = lastMove.getMovedPiece();

            System.out.println("En passant move made.");
            System.out.println("Captured piece " + capturedPiece  +
                    " with " + capturingPiece +
                    " from " + thisMove.getStart() + " to " + thisMove.getEnd());
            insertNewPosition(lastMove.getPositionAfterMove().positionNumber, game.FENNotation());
            insertNewMove(lastMove.getMoveNumber(), !game.whiteMoves ? "White" : "Black", lastMove.getMoveNotation());
            game.display();
        } else {
            System.out.println("Invalid move.");
        }
    }

    /**
     * Handles a piece capture, ensuring it is valid based on the game rules.
     *
     * @param clickedSquare the square containing the target piece to be captured.
     */
    private static void handleCapture(Square clickedSquare) {
        Piece selectedPiece = selectedSquare.getPiece();
        Piece targetPiece = clickedSquare.getPiece();

        if (targetPiece.color != selectedPiece.color) {
            if (selectedPiece.allowedCapture(selectedSquare, clickedSquare, game)) {
                selectedPiece.makeCapture(selectedSquare, clickedSquare, game);

                Move lastMove = game.gameHistory.get(game.gameHistory.size() - 1);
                System.out.println("Captured piece " + targetPiece +
                        " with " + lastMove.getMovedPiece() +
                        " from " + lastMove.getStart() + " to " + lastMove.getEnd());
                insertNewPosition(lastMove.getPositionAfterMove().positionNumber, game.FENNotation());
                insertNewMove(lastMove.getMoveNumber(), !game.whiteMoves ? "White" : "Black", lastMove.getMoveNotation());

                boolean isMyCapture = selectedPiece.color == game.getMe().playerColor;
                editCapturedPieces(isMyCapture, targetPiece.toString());
                game.display();
            } else {
                System.out.println("Invalid capture.");
            }
        } else {
            System.out.println("Invalid capture. You cannot capture your own piece.");
        }
    }

    /**
     * Retrieves the appropriate icon for a given chess piece.
     *
     * @param piece the chess piece for which the icon is needed.
     * @return the ImageIcon representing the piece.
     */
    private static ImageIcon getPieceIcon(Piece piece) {
        if (piece instanceof Pawn) return piece.color == Game.Color.WHITE ? WHITE_PAWN : BLACK_PAWN;
        if (piece instanceof Rook) return piece.color == Game.Color.WHITE ? WHITE_ROOK : BLACK_ROOK;
        if (piece instanceof Knight) return piece.color == Game.Color.WHITE ? WHITE_KNIGHT : BLACK_KNIGHT;
        if (piece instanceof Bishop) return piece.color == Game.Color.WHITE ? WHITE_BISHOP : BLACK_BISHOP;
        if (piece instanceof Queen) return piece.color == Game.Color.WHITE ? WHITE_QUEEN : BLACK_QUEEN;
        if (piece instanceof King) return piece.color == Game.Color.WHITE ? WHITE_KING : BLACK_KING;
        return null;
    }

    /**
     * Loads an icon from a specified resource path.
     *
     * @param resourcePath the path to the resource file.
     * @return the ImageIcon loaded from the resource, or null if the resource is not found.
     */
    private static ImageIcon loadIcon(String resourcePath) {
        URL resource = App.class.getClassLoader().getResource(resourcePath);
        if (resource == null) {
            System.err.println("Resource not found: " + resourcePath);
            return null;
        }
        return new ImageIcon(resource);
    }

    /**
     * Scales an icon to fit within a button, maintaining its aspect ratio.
     *
     * @param originalIcon the original icon to be scaled.
     * @param button       the button for which the icon is scaled.
     * @return the scaled ImageIcon.
     */
    private static ImageIcon scaleIcon(ImageIcon originalIcon, JButton button) {
        // Get the button's size after it's been added to the layout
        int buttonWidth = button.getWidth();
        int buttonHeight = button.getHeight();

        // Ensure button size is valid
        if (buttonWidth == 0 || buttonHeight == 0) {
            // Avoid scaling to a zero size, fallback to default size
            return originalIcon;
        }

        // Resize the icon to match button size, keeping aspect ratio
        Image originalImage = originalIcon.getImage();
        Image scaledImage = originalImage.getScaledInstance((int) (0.7 * buttonWidth), (int) (0.7 * buttonHeight), Image.SCALE_SMOOTH);

        // Return the resized icon
        return new ImageIcon(scaledImage);
    }
}