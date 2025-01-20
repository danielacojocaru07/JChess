package elements;

import pieces.*;

/// @author dana

/**
 * Represents the chessboard's position at any given time in the game.
 * It includes the current state of the board, such as piece placement, and is responsible for displaying the board.
 */
public class Position {
    /// ANSI escape code for white background color.
    public static final String WHITE_BACKGROUND = "\u001B[47m";
    /// ANSI escape code for black background color.
    public static final String BLACK_BACKGROUND = "\u001B[40m";
    /// ANSI escape code to reset text formatting.
    public static final String RESET = "\u001B[0m";
    /// A 2D array representing the board of squares (8x8).
    public Square[][] board;
    /// The position number used to uniquely identify the position in the game's history.
    public int positionNumber;

    /**
     * Constructs a new {@code Position} with an empty board and sets up the starting position of the game.
     */
    public Position() {
        this.board = new Square[8][8];
        startPosition();
        this.positionNumber = 0;
    }

    /**
     * Initializes the board with the starting positions of the chess pieces.
     */
    private void startPosition() {
        // Creating the board with alternating square colors
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                this.board[i][j] = new Square();
                board[i][j].setIsEmpty(true);
                board[i][j].setRank(i + 1);
                board[i][j].setFile((char) ('a' + j));

                if ((i % 2 == 0 && j % 2 == 0) || (i % 2 != 0 && j % 2 != 0)) {
                    board[i][j].setColor(Game.Color.BLACK);
                } else {
                    board[i][j].setColor(Game.Color.WHITE);
                }
            }
        }

        // Placing pawns in their initial positions
        for (int i = 0; i < 8; i++) {
            board[1][i].setIsEmpty(false);
            board[1][i].setPiece(new Pawn(Game.Color.WHITE));

            board[6][i].setIsEmpty(false);
            board[6][i].setPiece(new Pawn(Game.Color.BLACK));

            board[0][i].setIsEmpty(false);
            board[7][i].setIsEmpty(false);
        }

        // Placing rooks in their initial positions
        board[0][0].setPiece(new Rook(Game.Color.WHITE));
        board[0][7].setPiece(new Rook(Game.Color.WHITE));
        board[7][0].setPiece(new Rook(Game.Color.BLACK));
        board[7][7].setPiece(new Rook(Game.Color.BLACK));

        // Placing knights in their initial positions
        board[0][1].setPiece(new Knight(Game.Color.WHITE));
        board[0][6].setPiece(new Knight(Game.Color.WHITE));
        board[7][1].setPiece(new Knight(Game.Color.BLACK));
        board[7][6].setPiece(new Knight(Game.Color.BLACK));

        // Placing bishops in their initial positions
        board[0][2].setPiece(new Bishop(Game.Color.WHITE));
        board[0][5].setPiece(new Bishop(Game.Color.WHITE));
        board[7][2].setPiece(new Bishop(Game.Color.BLACK));
        board[7][5].setPiece(new Bishop(Game.Color.BLACK));

        // Placing queens in their initial positions
        board[0][3].setPiece(new Queen(Game.Color.WHITE));
        board[7][3].setPiece(new Queen(Game.Color.BLACK));

        // Placing kings in their initial positions
        board[0][4].setPiece(new King(Game.Color.WHITE));
        board[7][4].setPiece(new King(Game.Color.BLACK));
    }

    /**
     * Returns a string representation of the rank (row) and file (column) of a given square in the game.
     * The string will include the piece occupying the square, if any, and the appropriate background color.
     *
     * @param i The rank (row) of the board.
     * @param j The file (column) of the board.
     * @param game The current game instance used to access the current position.
     * @return A string representing the square's contents.
     */
    public String toStringRank(int i, int j, Game game) {
        String rank = "";
        Square square = game.currentPosition.board[i][j];
        if (square.getIsEmpty()) {
            rank += (square.getColor() == Game.Color.WHITE) ? WHITE_BACKGROUND + "   " : BLACK_BACKGROUND + "   ";
        } else {
            pieces.Piece piece = square.getPiece();
            rank += (square.getColor() == Game.Color.WHITE ? WHITE_BACKGROUND : BLACK_BACKGROUND) + " " + piece.toString() + " ";
        }
        return rank;
    }

    /**
     * Returns a string representation of the entire board, displaying each square with its piece and color.
     * The rank and file labels are included for visual clarity.
     *
     * @param game The current game instance used to access the current position and captured pieces.
     * @return A formatted string representing the full board or an error message if the game is null.
     */
    public String toString(Game game) {
        if (game == null) {
            return "Error: Game instance is null!";
        }

        StringBuilder position = new StringBuilder();

        if (game.getMe().playerColor == Game.Color.WHITE) {
            for (int i = 7; i >= 0; i--) {
                // Displaying the rank number
                position.append(game.currentPosition.board[i][0].getRank()).append(" ");
                for (int j = 0; j < 8; j++) position.append(toStringRank(i, j, game));
                position.append(RESET + "\n");
            }
            // Displaying the file
            position.append("  ");
            for (int i = 0; i < 8; i++) position.append(" ").append(game.currentPosition.board[0][i].getFile()).append(" ");
        } else {
            for (int i = 0; i < 8; i++) {
                // Displaying the rank number
                position.append(game.currentPosition.board[i][0].getRank()).append(" ");
                for (int j = 7; j >= 0; j--) position.append(toStringRank(i, j, game));
                position.append(RESET + "\n");
            }
            // Displaying the file
            position.append("  ");
            for (int i = 7; i >= 0; i--) position.append(" ").append(game.currentPosition.board[0][i].getFile()).append(" ");
        }

        StringBuilder myCapturedPieces = new StringBuilder();
        StringBuilder opponentCapturedPieces = new StringBuilder();

        // Check if getMeCapturedPieces() is null or empty
        if (game.getMeCapturedPieces() == null || game.getMeCapturedPieces().isEmpty()) {
            myCapturedPieces.append("-");
        } else {
            for (Piece piece : game.getMeCapturedPieces()) {
                myCapturedPieces.append(piece.toString()).append(" ");
            }
        }

        // Check if getOpponentCapturedPieces() is null or empty
        if (game.getOpponentCapturedPieces() == null || game.getOpponentCapturedPieces().isEmpty()) {
            opponentCapturedPieces.append("-");
        } else {
            for (Piece piece : game.getOpponentCapturedPieces()) {
                opponentCapturedPieces.append(piece.toString()).append(" ");
            }
        }

        position.append("\nMy captured pieces: ")
                .append(myCapturedPieces)
                .append("\nOpponent captured pieces: ")
                .append(opponentCapturedPieces);

        return position.toString();
    }

    /**
     * Creates a copy of the current position, including the state of each square and the position number.
     *
     * @return A new Position object that is a copy of the current position.
     */
    public Position copy() {
        Position copy = new Position();

        copy.board = new Square[8][8];
        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                copy.board[rank][file] = this.board[rank][file].copy();
            }
        }

        copy.positionNumber = this.positionNumber;

        return copy;
    }
}