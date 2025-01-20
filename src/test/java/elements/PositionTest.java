package elements;

import pieces.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PositionTest {

    private Position position;
    private Game game;

    @BeforeEach
    public void setUp() {
        // Set up a game and position for testing
        game = new Game(Player.ColorOption.WHITE);
        position = game.currentPosition; // Assuming the game's starting position is used
    }

    @Test
    public void testStartPosition() {
        // Test if the initial board setup is correct
        // Check pawns' positions
        for (int i = 0; i < 8; i++) {
            assertNotNull(position.board[1][i].getPiece()); // White pawns should be at row 1
            assertNotNull(position.board[6][i].getPiece()); // Black pawns should be at row 6
        }

        // Check other pieces' positions
        assertTrue(position.board[0][0].getPiece() instanceof Rook); // White Rooks at (0, 0) and (0, 7)
        assertTrue(position.board[0][7].getPiece() instanceof Rook);
        assertTrue(position.board[7][0].getPiece() instanceof Rook); // Black Rooks at (7, 0) and (7, 7)
        assertTrue(position.board[7][7].getPiece() instanceof Rook);

        assertTrue(position.board[0][4].getPiece() instanceof King); // White King at (0, 4)
        assertTrue(position.board[7][4].getPiece() instanceof King); // Black King at (7, 4)
    }

    @Test
    public void testToStringRankWithPiece() {
        // Test that non-empty squares display correctly with the piece
        Square square = position.board[0][1]; // Should be a white knight at (0, 1)
        String expected = Position.WHITE_BACKGROUND + " " + square.getPiece().toString() + " ";
        assertEquals(expected, position.toStringRank(0, 1, game));
    }

    @Test
    public void testToStringGameNull() {
        // Test if the toString method handles null game correctly
        assertEquals("Error: Game instance is null!", position.toString(null));
    }
}
