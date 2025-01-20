package elements;

import pieces.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SquareTest {

    private Square square;

    @BeforeEach
    public void setUp() {
        // Initialize a square for testing
        square = new Square();
    }

    @Test
    public void testConstructor() {
        // Test that a new square is empty and has no piece
        assertTrue(square.getIsEmpty());
        assertNull(square.getPiece());
    }

    @Test
    public void testSetColor() {
        // Test setting the color of the square
        square.setColor(Game.Color.WHITE);
        assertEquals(Game.Color.WHITE, square.getColor());

        square.setColor(Game.Color.BLACK);
        assertEquals(Game.Color.BLACK, square.getColor());
    }

    @Test
    public void testSetRankAndFile() {
        // Test setting the rank and file of the square
        square.setRank(5);
        square.setFile('d');

        assertEquals(5, square.getRank());
        assertEquals('d', square.getFile());
    }

    @Test
    public void testSetIsEmpty() {
        // Test changing the state of whether the square is empty
        square.setIsEmpty(false);
        assertFalse(square.getIsEmpty());

        square.setIsEmpty(true);
        assertTrue(square.getIsEmpty());
    }

    @Test
    public void testSetPiece() {
        // Test setting a piece on the square
        Piece pawn = new Pawn(Game.Color.WHITE);
        square.setPiece(pawn);

        assertEquals(pawn, square.getPiece());
        assertTrue(square.getIsEmpty());

        // Test removing the piece
        square.setPiece(null);
        assertNull(square.getPiece());
        assertTrue(square.getIsEmpty());
    }

    @Test
    public void testCopy() {
        // Test copying the square
        square.setRank(3);
        square.setFile('c');
        square.setColor(Game.Color.BLACK);
        square.setIsEmpty(false);
        square.setPiece(new Pawn(Game.Color.BLACK));

        Square copiedSquare = square.copy();

        // Ensure that the copied square is not the same object
        assertNotSame(square, copiedSquare);

        // Ensure that all properties are copied correctly
        assertEquals(square.getRank(), copiedSquare.getRank());
        assertEquals(square.getFile(), copiedSquare.getFile());
        assertEquals(square.getColor(), copiedSquare.getColor());
    }

    @Test
    public void testToString() {
        // Test the toString method for square position representation
        square.setRank(1);
        square.setFile('a');
        assertEquals("a1", square.toString());

        square.setRank(8);
        square.setFile('h');
        assertEquals("h8", square.toString());
    }
}