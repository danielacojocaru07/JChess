package elements;

import pieces.Piece;

/// @author dana

/**
 * Represents a move in a chess game, including details such as the start and end squares,
 * the piece moved, move notation, and the resulting position after the move.
 */
public class Move {
    /// The square from which the move starts.
    private final Square start;
    /// The square to which the move ends.
    private final Square end;
    /// The number of this move in the sequence of the game.
    private final int moveNumber;
    /// The chess piece that is moved during this move.
    private final Piece movedPiece;
    /// The chess notation representing this move (e.g., "e4", "Nxf3").
    private final String moveNotation;
    /// The board position after this move is executed.
    private final Position positionAfterMove;


    /**
     * Constructs a {@code Move} object.
     *
     * @param start the starting square of the move.
     * @param end the ending square of the move.
     * @param moveNumber the number of the move in the game sequence.
     * @param movedPiece the piece that is moved.
     * @param moveNotation the chess notation representing the move, e.g., "e4", "Nxf3".
     * @param positionAfterMove the position of the board after this move is made.
     */
    public Move(Square start, Square end, int moveNumber, Piece movedPiece, String moveNotation, Position positionAfterMove) {
        this.start = start;
        this.end = end;
        this.moveNumber = moveNumber;
        this.movedPiece = movedPiece;
        this.moveNotation = moveNotation;
        this.positionAfterMove = positionAfterMove;
    }

    /**
     * Gets the starting square of the move.
     *
     * @return the Square representing the starting position of the piece.
     */
    public Square getStart() {
        return start;
    }

    /**
     * Gets the ending square of the move.
     *
     * @return the Square representing the destination of the piece.
     */
    public Square getEnd() {
        return end;
    }

    /**
     * Gets the number of this move in the game's sequence.
     *
     * @return the move number as an integer.
     */
    public int getMoveNumber() {
        return moveNumber;
    }

    /**
     * Gets the piece that was moved.
     *
     * @return the Piece involved in the move.
     */
    public Piece getMovedPiece() {
        return movedPiece;
    }

    /**
     * Gets the notation of the move in standard chess notation.
     *
     * @return a String representing the move in standard chess notation.
     */
    public String getMoveNotation() {
        return moveNotation;
    }

    /**
     * Gets the board position after this move is executed.
     *
     * @return the Position object representing the state of the board after the move.
     */
    public Position getPositionAfterMove() {
        return positionAfterMove;
    }

    /**
     * Converts this move into a string representation.
     * The string includes the resulting board state, the move number, and the move notation.
     *
     * @param game the current game context.
     * @return a string describing this move.
     */
    public String toString(Game game) {
        return positionAfterMove.toString(game) + "\n" + moveNumber + ". " + moveNotation;
    }

    /**
     * Creates a deep copy of this move, including its associated pieces and board position.
     *
     * @return a new Move object that is a clone of the current move.
     */
    public Move copy() {
        return new Move(
                this.start.copy(),
                this.end.copy(),
                this.moveNumber,
                this.movedPiece.copy(),
                this.moveNotation,
                this.positionAfterMove.copy()
        );
    }
}