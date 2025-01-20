package pieces;

import elements.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PawnTest {

    private Game game;
    private Position position;
    private Square startSquare;
    private Square endSquareWhite;

    @BeforeEach
    void setUp() {
        game = new Game(Player.ColorOption.WHITE);
        position = new Position();
        game.currentPosition = position;
        startSquare = position.board[1][0];  // White pawn starting position
        endSquareWhite = position.board[2][0]; // White pawn moves 1 square
    }

    @Test
    void testPawnInitialMoveWhite() {
        Pawn pawn = new Pawn(Game.Color.WHITE);
        assertTrue(pawn.allowedMove(startSquare, endSquareWhite, game), "Pawn should be able to move 2 squares forward on first move.");
    }

    @Test
    void testPawnInitialMoveBlack() {
        Pawn pawn = new Pawn(Game.Color.BLACK);
        assertTrue(pawn.allowedMove(position.board[6][0], position.board[5][0], game), "Black pawn should be able to move 1 square forward.");
    }

    @Test
    void testPawnMoveAfterFirst() {
        Pawn pawn = new Pawn(Game.Color.WHITE);
        pawn.isFirstMove = false;
        assertTrue(pawn.allowedMove(startSquare, position.board[2][0], game), "Pawn should be able to move 1 square after its first move.");
    }

    @Test
    void testEnPassantWhite() {
        // Simulate opponent's pawn moving two squares
        Pawn opponentPawn = new Pawn(Game.Color.BLACK);
        opponentPawn.makeMove(position.board[6][1], position.board[4][1], game);

        // Now check if White pawn can perform en passant
        Pawn whitePawn = new Pawn(Game.Color.WHITE);
        boolean enPassant = whitePawn.enPassant(position.board[4][0], game);
        assertTrue(enPassant, "White pawn should be able to perform en passant.");
    }

    @Test
    void testEnPassantBlack() {
        // Simulate opponent's pawn moving two squares
        Pawn opponentPawn = new Pawn(Game.Color.WHITE);
        opponentPawn.makeMove(position.board[1][1], position.board[3][1], game);

        // Now check if Black pawn can perform en passant
        Pawn blackPawn = new Pawn(Game.Color.BLACK);
        boolean enPassant = blackPawn.enPassant(position.board[3][0], game);
        assertTrue(enPassant, "Black pawn should be able to perform en passant.");
    }

    @Test
    void testCopyPawn() {
        Pawn originalPawn = new Pawn(Game.Color.WHITE);
        Pawn copiedPawn = (Pawn) originalPawn.copy();

        assertEquals(originalPawn.color, copiedPawn.color, "The copied pawn should have the same color.");
        assertEquals(originalPawn.isFirstMove, copiedPawn.isFirstMove, "The copied pawn should have the same first move status.");
    }
}