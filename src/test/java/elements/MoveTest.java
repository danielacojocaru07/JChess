package elements;

import pieces.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MoveTest {
    Game game = new Game(Player.ColorOption.WHITE);

    private Square startSquare;
    private Square endSquare;
    private Piece movedPiece;
    private Position positionAfterMove;
    private Move move = new Move(
            game.currentPosition.board[4][1],
            game.currentPosition.board[4][3],
            1,
            game.currentPosition.board[4][1].getPiece(),
            "e4",
            game.currentPosition
    );

    @BeforeEach
    public void setUp() {
        // Setup mock or real objects required for testing.
        startSquare = new Square();
        endSquare = new Square();
        movedPiece = new Pawn(Game.Color.WHITE); // Example piece.
        positionAfterMove = new Position(); // Assuming Position class has a constructor.

        // Creating a Move object
        move = new Move(startSquare, endSquare, 1, movedPiece, "e4", positionAfterMove);
    }

    @Test
    public void testGetStart() {
        assertEquals(startSquare, move.getStart());
    }

    @Test
    public void testGetEnd() {
        assertEquals(endSquare, move.getEnd());
    }

    @Test
    public void testGetMoveNumber() {
        assertEquals(1, move.getMoveNumber());
    }

    @Test
    public void testGetMovedPiece() {
        assertEquals(movedPiece, move.getMovedPiece());
    }

    @Test
    public void testGetMoveNotation() {
        assertEquals("e4", move.getMoveNotation());
    }

    @Test
    public void testGetPositionAfterMove() {
        assertEquals(positionAfterMove, move.getPositionAfterMove());
    }

    @Test
    public void testToString() {
        // Assuming positionAfterMove.toString(game) returns a string
        String expectedString = positionAfterMove.toString(game) + "\n1. e4";
        assertEquals(expectedString, move.toString(game));
    }
}