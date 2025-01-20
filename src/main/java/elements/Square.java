package elements;

import pieces.Piece;

/// @author dana

/**
 * Represents a single square on the chessboard. A square contains information about its color,
 * rank, file, whether it is empty, and the piece occupying it (if any).
 */
public class Square{
    /// The color of the square (either black or white).
    private Game.Color color;
    /// The rank (row) of the square on the chessboard (1 to 8).
    private int rank;
    /// The file (column) of the square on the chessboard (a to h).
    private char file;
    /// Indicates whether the square is empty (i.e., contains no piece).
    private boolean isEmpty;
    /// The piece occupying the square, if any.
    private Piece piece;

    /**
     * Constructs a new {@code Square}, initially empty with no piece placed on it.
     */
    public Square() {
        this.isEmpty = true;
        this.piece = null;
    }

    /**
     * Returns the color of the square.
     *
     * @return The color of the square.
     */
    public Game.Color getColor() {
        return color;
    }

    /**
     * Returns the rank (row) of the square.
     *
     * @return The rank of the square.
     */
    public int getRank() {
        return rank;
    }

    /**
     * Returns the file (column) of the square.
     *
     * @return The file of the square.
     */
    public char getFile() {
        return file;
    }

    /**
     * Returns whether the square is empty (i.e., contains no piece).
     *
     * @return True if the square is empty, false otherwise.
     */
    public boolean getIsEmpty() {
        return isEmpty;
    }

    /**
     * Returns the piece occupying the square, if any.
     *
     * @return The piece on the square, or null if the square is empty.
     */
    public Piece getPiece() {
        return piece;
    }

    /**
     * Sets the color of the square.
     *
     * @param color The color of the square.
     */
    public void setColor(Game.Color color) {
        this.color = color;
    }

    /**
     * Sets the rank (row) of the square.
     *
     * @param rank The rank of the square.
     */
    public void setRank(int rank) {
        this.rank = rank;
    }

    /**
     * Sets the file (column) of the square.
     *
     * @param file The file of the square.
     */
    public void setFile(char file) {
        this.file = file;
    }

    /**
     * Sets whether the square is empty or not.
     *
     * @param empty True if the square is empty, false otherwise.
     */
    public void setIsEmpty(boolean empty) {
        this.isEmpty = empty;
    }

    /**
     * Sets the piece occupying the square.
     *
     * @param piece The piece to place on the square, or null to leave it empty.
     */
    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    /**
     * Creates a copy of the current square, including all its properties and piece (if any).
     *
     * @return A new Square object that is a copy of the current square.
     */
    public Square copy() {
        Square copy = new Square();
        copy.setIsEmpty(this.getIsEmpty());
        copy.setRank(this.getRank());
        copy.setFile(this.getFile());
        copy.setColor(this.getColor());
        copy.setPiece(this.getPiece() != null ? this.getPiece().copy() : null);
        return copy;
    }

    /**
     * Returns a string representation of the square's position using its file and rank (e.g., "a1", "h8").
     *
     * @return A string representing the square's position.
     */
    @Override
    public String toString() {
        return file + String.valueOf(rank);
    }
}