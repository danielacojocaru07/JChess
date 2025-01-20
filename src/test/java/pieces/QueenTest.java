package pieces;

import elements.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class QueenTest {
    private Game game;
    private Queen whiteQueen;
    private Square startSquare;
    private Square endSquare;

    @BeforeEach
    void setUp() {
        // Initialize the game and pieces
        game = new Game(Player.ColorOption.WHITE);
        whiteQueen = new Queen(Game.Color.WHITE);
        startSquare = new Square(); // Example start square for Queen (White Queen starting position)
        endSquare = new Square(); // Example end square for Queen
    }

    @Test
    void testCheckEmptyPathHorizontal() {
        // Set up a situation where the path is empty horizontally
        endSquare = new Square(); // Horizontal move from 'd' to 'h'
        boolean result = whiteQueen.checkEmptyPath(startSquare, endSquare, game);
        assertTrue(result, "The path should be empty horizontally.");
    }

    @Test
    void testCheckEmptyPathVertical() {
        // Set up a situation where the path is empty vertically
        endSquare = new Square(); // Vertical move from '1d' to '4d'
        boolean result = whiteQueen.checkEmptyPath(startSquare, endSquare, game);
        assertTrue(result, "The path should be empty vertically.");
    }

    @Test
    void testCheckEmptyPathDiagonal() {
        // Set up a situation where the path is empty diagonally
        endSquare = new Square(); // Diagonal move from '1d' to '4g'
        boolean result = whiteQueen.checkEmptyPath(startSquare, endSquare, game);
        assertTrue(result, "The path should be empty diagonally.");
    }

    @Test
    void testCopy() {
        // Test the copy method for the Queen
        Piece copiedQueen = whiteQueen.copy();
        assertNotNull(copiedQueen, "The copied Queen should not be null.");
        assertEquals(whiteQueen.getClass(), copiedQueen.getClass(), "The copied piece should be of the same class.");
        assertEquals(whiteQueen.color, copiedQueen.color, "The copied Queen should have the same color.");
    }
}