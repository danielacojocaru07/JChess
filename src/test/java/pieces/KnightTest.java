package pieces;

import elements.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class KnightTest {

    private Game game;
    private Position position;
    private Square startSquareWhite;
    private Square endSquareValid;
    private Square endSquareInvalid;
    private Square endSquareCapture;

    @BeforeEach
    void setUp() {
        game = new Game(Player.ColorOption.WHITE);
        position = new Position();
        game.currentPosition = position;
        startSquareWhite = position.board[0][1]; // White knight starting position
        endSquareValid = position.board[2][2]; // Valid knight move
        endSquareInvalid = position.board[3][3]; // Invalid knight move
        endSquareCapture = position.board[2][2]; // Square to capture opponent's piece
    }

    @Test
    void testKnightInitialMoveWhite() {
        Knight knight = new Knight(Game.Color.WHITE);
        assertTrue(knight.allowedMove(startSquareWhite, endSquareValid, game), "Knight should be able to move in an L-shape.");
    }

    @Test
    void testKnightInvalidMove() {
        Knight knight = new Knight(Game.Color.WHITE);
        assertFalse(knight.allowedMove(startSquareWhite, endSquareInvalid, game), "Knight should not be able to move in a non L-shape.");
    }

    @Test
    void testKnightCaptureNotAllowedSameColor() {
        // Simulate placing a friendly piece on the destination square
        position.board[2][2].setPiece(new Pawn(Game.Color.WHITE)); // Friendly piece
        Knight knight = new Knight(Game.Color.WHITE);

        assertFalse(knight.allowedCapture(startSquareWhite, endSquareCapture, game), "Knight should not be able to capture a piece of the same color.");
    }

    @Test
    void testKnightCaptureNotAllowedEmpty() {
        // Simulate an empty destination square
        position.board[2][2].setPiece(null);
        Knight knight = new Knight(Game.Color.WHITE);

        assertFalse(knight.allowedCapture(startSquareWhite, endSquareCapture, game), "Knight should not be able to capture an empty square.");
    }

    @Test
    void testKnightCopy() {
        Knight originalKnight = new Knight(Game.Color.WHITE);
        Knight copiedKnight = (Knight) originalKnight.copy();

        assertEquals(originalKnight.color, copiedKnight.color, "The copied knight should have the same color.");
    }
}
