package elements;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    private Game game;

    @BeforeEach
    public void setUp() {
        // Initialize the game with a player color, here White for the user
        game = new Game(Player.ColorOption.WHITE);
    }

    @Test
    public void testInitialGameState() {
        // Test that the initial state of the game is correct
        assertNotNull(game.getMe(), "The user player should be initialized.");
        assertNotNull(game.getMeCapturedPieces(), "Captured pieces list for the user should be initialized.");
        assertNotNull(game.getOpponentCapturedPieces(), "Captured pieces list for the opponent should be initialized.");
        assertTrue(game.whiteMoves, "It should be White's turn to move initially.");
    }

    @Test
    public void testIsCheckForWhite() {
        // Test that the check state for White is false initially (not in check)
        assertFalse(game.isCheckForWhite(), "White should not be in check at the start of the game.");
    }

    @Test
    public void testIsCheckForBlack() {
        // Test that the check state for Black is false initially (not in check)
        assertFalse(game.isCheckForBlack(), "Black should not be in check at the start of the game.");
    }

    @Test
    public void testFENNotation() {
        // Test the FEN notation output for the initial game state
        String fen = game.FENNotation();
        assertNotNull(fen, "FEN notation should not be null.");
        assertTrue(fen.contains("w "), "The FEN notation should contain 'w ' indicating it's White's turn.");
    }

    @Test
    public void testIsDraw() {
        // Test the draw condition (initially it should not be a draw)
        assertFalse(game.isDraw(), "The game should not be a draw initially.");
    }

    @Test
    public void testCopy() {
        // Test the copy method, ensuring that the copied game is not the same as the original
        Game gameCopy = game.copy();
        assertNotNull(gameCopy, "The game copy should not be null.");
        assertNotSame(game, gameCopy, "The copied game should be a different object.");
    }

    @Test
    public void testInsufficientMaterial() {
        // Test the insufficient material condition for both colors
        assertFalse(game.insufficientMaterial(Game.Color.WHITE), "The game should not be in an insufficient material state for White.");
        assertFalse(game.insufficientMaterial(Game.Color.BLACK), "The game should not be in an insufficient material state for Black.");
    }

    @Test
    public void testDestroy() {
        // Test the destroy method
        game.destroy();
        assertNull(game.currentPosition, "The game board should be null after destroying the game.");
        assertTrue(game.gameHistory.isEmpty(), "The game history should be empty after destroying the game.");
        assertNull(game.getMe(), "The user player should be null after destroying the game.");
    }

    @Test
    public void testPossibleMoveFromCheck() {
        // Test that possibleMoveFromCheck returns the correct value (initially no check)
        assertFalse(game.possibleMoveFromCheck(Game.Color.WHITE), "White should not have possible moves to escape check initially.");
        assertFalse(game.possibleMoveFromCheck(Game.Color.BLACK), "Black should not have possible moves to escape check initially.");
    }
}

