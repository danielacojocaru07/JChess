package pieces;

import elements.*;

import java.util.List;

/// @author dana

/**
 * Represents a chess piece and provides methods for move validation, movement, capturing, and move notation.
 */
public abstract class Piece {
    // Constants for the white and black pieces' symbols.
    public static final String WHITE_PAWN = "P";
    public static final String WHITE_ROOK = "R";
    public static final String WHITE_KNIGHT = "N";
    public static final String WHITE_BISHOP = "B";
    public static final String WHITE_QUEEN = "Q";
    public static final String WHITE_KING = "K";

    public static final String BLACK_PAWN = "p";
    public static final String BLACK_ROOK = "r";
    public static final String BLACK_KNIGHT = "n";
    public static final String BLACK_BISHOP = "b";
    public static final String BLACK_QUEEN = "q";
    public static final String BLACK_KING = "k";

    /// The color of the piece (either white or black).
    public Game.Color color;

    /**
     * Constructs a {@code Piece} with the specified color.
     *
     * @param color The color of the piece (either white or black).
     */
    public Piece(Game.Color color) {
        this.color = color;
    }

    /**
     * Checks if the move from the start square to the end square would stop a check.
     *
     * @param start The start square of the move.
     * @param end The end square of the move.
     * @param game The current game state.
     * @return True if the move stops a check, otherwise false.
     */
    public boolean stopCheck(Square start, Square end, Game game) {
        Game copiedGame = game.copy();

        Position position = copiedGame.currentPosition;
        int startRank = start.getRank() - 1;
        int startFile = start.getFile() - 'a';
        int endRank = end.getRank() - 1;
        int endFile = end.getFile() - 'a';

        if (allowedMove(start, end, copiedGame)) {
            // Move the piece to the end Square
            position.board[endRank][endFile].setIsEmpty(false);
            position.board[endRank][endFile].setPiece(position.board[startRank][startFile].getPiece());

            // Set the start Square empty
            position.board[startRank][startFile].setIsEmpty(true);
            position.board[startRank][startFile].setPiece(null);

            position.positionNumber++;
            copiedGame.currentPosition = position;

            Move newMove = new Move(
                    start,
                    end,
                    copiedGame.currentPosition.positionNumber,
                    this,
                    moveNotation(start, end, game, false),
                    copiedGame.currentPosition
            );

            // Update the game history
            copiedGame.gameHistory.add(newMove);

            copiedGame.whiteMoves = !copiedGame.whiteMoves;
        } else if (allowedCapture(start, end, copiedGame)) {
            if (color == copiedGame.getMe().playerColor) copiedGame.getMeCapturedPieces().add(position.board[endRank][endFile].getPiece());
            else copiedGame.getOpponentCapturedPieces().add(position.board[endRank][endFile].getPiece());

            // Move the piece to the end Square
            position.board[endRank][endFile].setIsEmpty(false);
            position.board[endRank][endFile].setPiece(position.board[startRank][startFile].getPiece());

            // Set the start Square empty
            position.board[startRank][startFile].setIsEmpty(true);
            position.board[startRank][startFile].setPiece(null);

            position.positionNumber++;
            copiedGame.currentPosition = position;

            Move newMove = new Move(
                    start,
                    end,
                    copiedGame.currentPosition.positionNumber,
                    this,
                    moveNotation(start, end, game, true),
                    copiedGame.currentPosition
            );

            // Update the copied game history
            copiedGame.gameHistory.add(newMove);

            copiedGame.whiteMoves = !copiedGame.whiteMoves;
        } else {
            return false;
        }

        boolean stillInCheck = (color == Game.Color.WHITE && copiedGame.isCheckForWhite()) || (color == Game.Color.BLACK && copiedGame.isCheckForBlack());

        copiedGame.destroy();

        return !stillInCheck;
    }

    /**
     * Abstract method to determine if a move is allowed based on the piece's movement rules.
     *
     * @param start The start square of the move.
     * @param end The end square of the move.
     * @param game The current game state.
     * @return True if the move is allowed, otherwise false.
     */
    public abstract boolean allowedMove(Square start, Square end, Game game);

    /**
     * Makes a move from the start square to the end square if the move is valid and does not put the player in check.
     *
     * @param start The start square of the move.
     * @param end The end square of the move.
     * @param game The current game state.
     */
    public void makeMove(Square start, Square end, Game game) {
        Position position = game.currentPosition;

        if (allowedMove(start, end, game) && stopCheck(start, end, game)) {
            int startRank = start.getRank() - 1;
            int startFile = start.getFile() - 'a';
            int endRank = end.getRank() - 1;
            int endFile = end.getFile() - 'a';

            // Move the piece to the end Square
            position.board[endRank][endFile].setIsEmpty(false);
            position.board[endRank][endFile].setPiece(position.board[startRank][startFile].getPiece());

            // Set the start Square empty
            position.board[startRank][startFile].setIsEmpty(true);
            position.board[startRank][startFile].setPiece(null);

            position.positionNumber++;
            if (this instanceof Pawn) game.halfMoveClock = 0;
            else game.halfMoveClock++;
            game.currentPosition = position;

            int moveNumber = (int) (Math.ceil((double) game.currentPosition.positionNumber / 2));

            String moveNotation = moveNotation(start, end, game, false);
            if (game.isCheckmateForWhite() || game.isCheckmateForBlack()) moveNotation += "#";
            else if (game.isCheckForWhite() || game.isCheckForBlack()) moveNotation += "+";

            Move newMove = new Move(start, end, moveNumber, this, moveNotation, game.currentPosition);

            // Update the game history
            game.gameHistory.add(newMove);

            game.whiteMoves = !game.whiteMoves;
        }
    }

    /**
     * Abstract method to determine if a capture is allowed based on the piece's movement rules.
     *
     * @param start The start square of the capture.
     * @param end The end square of the capture.
     * @param game The current game state.
     * @return True if the capture is allowed, otherwise false.
     */
    public abstract boolean allowedCapture(Square start, Square end, Game game);

    /**
     * Makes a capture from the start square to the end square if the capture is valid and does not put the player in check.
     *
     * @param start The start square of the capture.
     * @param end The end square of the capture.
     * @param game The current game state.
     */
    public void makeCapture(Square start, Square end, Game game) {
        Position position = game.currentPosition;

        if (allowedCapture(start, end, game) && stopCheck(start, end, game)) {
            int startRank = start.getRank() - 1;
            int startFile = start.getFile() - 'a';
            int endRank = end.getRank() - 1;
            int endFile = end.getFile() - 'a';

            if (color == game.getMe().playerColor) game.getMeCapturedPieces().add(position.board[endRank][endFile].getPiece());
            else game.getOpponentCapturedPieces().add(position.board[endRank][endFile].getPiece());

            // Move the piece to the end Square
            position.board[endRank][endFile].setIsEmpty(false);
            position.board[endRank][endFile].setPiece(position.board[startRank][startFile].getPiece());

            // Set the start Square empty
            position.board[startRank][startFile].setIsEmpty(true);
            position.board[startRank][startFile].setPiece(null);

            position.positionNumber++;
            game.halfMoveClock = 0;
            game.currentPosition = position;

            int moveNumber = (int) (Math.ceil((double) game.currentPosition.positionNumber / 2));

            String moveNotation = moveNotation(start, end, game, true);
            if (game.isCheckmateForWhite() || game.isCheckmateForBlack()) moveNotation += "#";
            else if (game.isCheckForWhite() || game.isCheckForBlack()) moveNotation += "+";

            Move newMove = new Move(start, end, moveNumber, this, moveNotation, game.currentPosition);

            // Update the game history
            game.gameHistory.add(newMove);

            game.whiteMoves = !game.whiteMoves;
        }
    }

    /**
     * Generates the algebraic notation for a move, including any disambiguation or capture indicators.
     *
     * @param start The start square of the move.
     * @param end The end square of the move.
     * @param game The current game state.
     * @param isCapture True if the move is a capture, otherwise false.
     * @return The move notation as a string.
     */
    private String moveNotation(Square start, Square end, Game game, boolean isCapture) {
        StringBuilder moveNotation = new StringBuilder();

        List<Square> mySquares = game.getPiecesOfColor(color);
        if (isCapture) {
            if (this instanceof Pawn) moveNotation.append(start.getFile()).append("x");
            if (this instanceof Knight) {
                moveNotation.append("N");
                // For ambiguities
                for (Square square : mySquares) {
                    Piece piece = square.getPiece();
                    if (piece instanceof Knight && piece.allowedCapture(square, end, game) && !square.equals(start)) {
                        // Check for disambiguation by file or rank
                        if (start.getFile() == square.getFile()) moveNotation.append(start.getRank());
                        else moveNotation.append(start.getFile());
                        break;
                    }
                }
                moveNotation.append("x");
            }
            if (this instanceof Bishop) {
                moveNotation.append("B");
                // For ambiguities
                for (Square square : mySquares) {
                    Piece piece = square.getPiece();
                    if (piece instanceof Bishop && piece.allowedCapture(square, end, game) && !square.equals(start)) {
                        // Check for disambiguation by file or rank
                        if (start.getFile() == square.getFile()) moveNotation.append(start.getRank());
                        else moveNotation.append(start.getFile());
                        break;
                    }
                }
                moveNotation.append("x");
            }
            if (this instanceof Rook) {
                moveNotation.append("R");
                // For ambiguities
                for (Square square : mySquares) {
                    Piece piece = square.getPiece();
                    if (piece instanceof Rook && piece.allowedCapture(square, end, game) && !square.equals(start)) {
                        // Check for disambiguation by file or rank
                        if (start.getFile() == square.getFile()) moveNotation.append(start.getRank());
                        else moveNotation.append(start.getFile());
                        break;
                    }
                }
                moveNotation.append("x");
            }
            if (this instanceof Queen) {
                moveNotation.append("Q");
                // For ambiguities
                for (Square square : mySquares) {
                    Piece piece = square.getPiece();
                    if (piece instanceof Queen && piece.allowedCapture(square, end, game) && !square.equals(start)) {
                        // Check for disambiguation by file or rank
                        if (start.getFile() == square.getFile()) moveNotation.append(start.getRank());
                        else moveNotation.append(start.getFile());
                        break;
                    }
                }
                moveNotation.append("x");
            }
            if (this instanceof King) moveNotation = new StringBuilder("Kx");
        } else {
            if (this instanceof Knight) {
                moveNotation.append("N");
                // For ambiguities
                for (Square square : mySquares) {
                    Piece piece = square.getPiece();
                    if (piece instanceof Knight && piece.allowedCapture(square, end, game) && !square.equals(start)) {
                        // Check for disambiguation by file or rank
                        if (start.getFile() == square.getFile()) moveNotation.append(start.getRank());
                        else moveNotation.append(start.getFile());
                        break;
                    }
                }
            }
            if (this instanceof Bishop) {
                moveNotation.append("B");
                // For ambiguities
                for (Square square : mySquares) {
                    Piece piece = square.getPiece();
                    if (piece instanceof Bishop && piece.allowedCapture(square, end, game) && !square.equals(start)) {
                        // Check for disambiguation by file or rank
                        if (start.getFile() == square.getFile()) moveNotation.append(start.getRank());
                        else moveNotation.append(start.getFile());
                        break;
                    }
                }
            }
            if (this instanceof Rook) {
                moveNotation.append("R");
                // For ambiguities
                for (Square square : mySquares) {
                    Piece piece = square.getPiece();
                    if (piece instanceof Rook && piece.allowedCapture(square, end, game) && !square.equals(start)) {
                        // Check for disambiguation by file or rank
                        if (start.getFile() == square.getFile()) moveNotation.append(start.getRank());
                        else moveNotation.append(start.getFile());
                        break;
                    }
                }
            }
            if (this instanceof Queen) {
                moveNotation.append("Q");
                // For ambiguities
                for (Square square : mySquares) {
                    Piece piece = square.getPiece();
                    if (piece instanceof Queen && piece.allowedCapture(square, end, game) && !square.equals(start)) {
                        // Check for disambiguation by file or rank
                        if (start.getFile() == square.getFile()) moveNotation.append(start.getRank());
                        else moveNotation.append(start.getFile());
                        break;
                    }
                }
            }
            if (this instanceof King) moveNotation = new StringBuilder("K");
        }
        moveNotation.append(end.getFile()).append(end.getRank());
        return moveNotation.toString();
    }

    /**
     * Creates a copy of the current piece.
     *
     * @return A new piece that is a copy of the current one.
     */
    public abstract Piece copy();

    /**
     * Returns the string representation of the piece (e.g., "P" for white pawn, "r" for black rook).
     *
     * @return The string representation of the piece.
     */
    @Override
    public String toString() {
        String pieceSymbol = "";
        if (this instanceof Pawn) pieceSymbol = (this.color == Game.Color.WHITE) ? WHITE_PAWN : BLACK_PAWN;
        if (this instanceof Rook) pieceSymbol = (this.color == Game.Color.WHITE) ? WHITE_ROOK : BLACK_ROOK;
        if (this instanceof Knight) pieceSymbol = (this.color == Game.Color.WHITE) ? WHITE_KNIGHT : BLACK_KNIGHT;
        if (this instanceof Bishop) pieceSymbol = (this.color == Game.Color.WHITE) ? WHITE_BISHOP : BLACK_BISHOP;
        if (this instanceof Queen) pieceSymbol = (this.color == Game.Color.WHITE) ? WHITE_QUEEN : BLACK_QUEEN;
        if (this instanceof King) pieceSymbol = (this.color == Game.Color.WHITE) ? WHITE_KING : BLACK_KING;
        return pieceSymbol;
    }
}