package pieces;

import elements.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RookTest {
    private Game game;
    private Rook whiteRook;
    private Square startSquare;
    private Square endSquare;

    @BeforeEach
    void setUp() {
        // Initialize the game and pieces
        game = new Game(Player.ColorOption.WHITE);
        whiteRook = new Rook(Game.Color.WHITE);
        startSquare = new Square(); // Example start square for Rook (White Rook starting position)
        endSquare = new Square(); // Example end square for Rook
    }

    @Test
    void testCheckEmptyPathHorizontal() {
        // Set up a situation where the path is empty horizontally
        endSquare = new Square(); // Horizontal move from 'a' to 'd'
        boolean result = whiteRook.checkEmptyPath(startSquare, endSquare, game);
        assertTrue(result, "The path should be empty horizontally.");
    }

    @Test
    void testCheckEmptyPathVertical() {
        // Set up a situation where the path is empty vertically
        endSquare = new Square(); // Vertical move from '1a' to '4a'
        boolean result = whiteRook.checkEmptyPath(startSquare, endSquare, game);
        assertTrue(result, "The path should be empty vertically.");
    }

    @Test
    void testCopy() {
        // Test the copy method for the Rook
        Piece copiedRook = whiteRook.copy();
        assertNotNull(copiedRook, "The copied Rook should not be null.");
        assertEquals(whiteRook.getClass(), copiedRook.getClass(), "The copied piece should be of the same class.");
        assertEquals(whiteRook.color, copiedRook.color, "The copied Rook should have the same color.");
        assertEquals(whiteRook.isFirstMove, ((Rook)copiedRook).isFirstMove, "The copied Rook should have the same first move status.");
    }
}