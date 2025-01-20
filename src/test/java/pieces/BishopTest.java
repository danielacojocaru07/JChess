package pieces;

import elements.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BishopTest {

    private Game game;
    private Position position;
    private Square startSquareWhite;
    private Square endSquareInvalid;
    private Square endSquareCapture;
    private Square endSquareBlocked;

    @BeforeEach
    void setUp() {
        game = new Game(Player.ColorOption.WHITE);
        position = new Position();
        game.currentPosition = position;
        startSquareWhite = position.board[0][2]; // White bishop starting position
        endSquareInvalid = position.board[3][3]; // Invalid bishop move (not diagonal)
        endSquareCapture = position.board[3][5]; // Square to capture opponent's piece
        endSquareBlocked = position.board[2][4]; // Square blocked by another piece
    }

    @Test
    void testBishopInvalidMove() {
        Bishop bishop = new Bishop(Game.Color.WHITE);
        assertFalse(bishop.allowedMove(startSquareWhite, endSquareInvalid, game), "Bishop should not be able to move in a non-diagonal direction.");
    }

    @Test
    void testBishopCaptureNotAllowedSameColor() {
        // Simulate placing a friendly piece on the destination square
        position.board[3][5].setPiece(new Pawn(Game.Color.WHITE)); // Friendly piece
        Bishop bishop = new Bishop(Game.Color.WHITE);

        assertFalse(bishop.allowedCapture(startSquareWhite, endSquareCapture, game), "Bishop should not be able to capture a piece of the same color.");
    }

    @Test
    void testBishopCaptureNotAllowedEmpty() {
        // Simulate an empty destination square
        position.board[3][5].setPiece(null);
        Bishop bishop = new Bishop(Game.Color.WHITE);

        assertFalse(bishop.allowedCapture(startSquareWhite, endSquareCapture, game), "Bishop should not be able to capture an empty square.");
    }

    @Test
    void testBishopMoveBlocked() {
        // Simulate placing a piece on the path of the bishop's move
        position.board[2][4].setPiece(new Pawn(Game.Color.WHITE)); // Blocked by friendly piece
        Bishop bishop = new Bishop(Game.Color.WHITE);

        assertFalse(bishop.allowedMove(startSquareWhite, endSquareBlocked, game), "Bishop should not be able to move through a blocked path.");
    }

    @Test
    void testBishopCopy() {
        Bishop originalBishop = new Bishop(Game.Color.WHITE);
        Bishop copiedBishop = (Bishop) originalBishop.copy();

        assertEquals(originalBishop.color, copiedBishop.color, "The copied bishop should have the same color.");
    }
}