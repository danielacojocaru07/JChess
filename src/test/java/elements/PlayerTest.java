package elements;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    private Player whitePlayer;
    private Player blackPlayer;
    private Player randomPlayer;

    @BeforeEach
    public void setUp() {
        // Setup players with different color options
        whitePlayer = new Player(Player.ColorOption.WHITE);
        blackPlayer = new Player(Player.ColorOption.BLACK);
        randomPlayer = new Player(Player.ColorOption.RANDOM);
    }

    @Test
    public void testWhitePlayerConstructor() {
        assertEquals(Game.Color.WHITE, whitePlayer.playerColor);
    }

    @Test
    public void testBlackPlayerConstructor() {
        assertEquals(Game.Color.BLACK, blackPlayer.playerColor);
    }

    @Test
    public void testRandomPlayerConstructor() {
        // Test if the random player is assigned a valid color (WHITE or BLACK)
        assertTrue(randomPlayer.playerColor == Game.Color.WHITE || randomPlayer.playerColor == Game.Color.BLACK);
    }

    @Test
    public void testCopy() {
        // Create a copy of the white player and check if it's not the same reference but has the same color
        Player copiedPlayer = whitePlayer.copy();
        assertNotSame(whitePlayer, copiedPlayer);
        assertEquals(whitePlayer.playerColor, copiedPlayer.playerColor);
        assertEquals(whitePlayer.colorOption, copiedPlayer.colorOption);
    }

    @Test
    public void testRandomPlayerColorAssignment() {
        // Create multiple random players to test that the random color option works correctly
        Player randomPlayer1 = new Player(Player.ColorOption.RANDOM);
        Player randomPlayer2 = new Player(Player.ColorOption.RANDOM);

        assertTrue(randomPlayer1.playerColor == Game.Color.WHITE || randomPlayer1.playerColor == Game.Color.BLACK);
        assertTrue(randomPlayer2.playerColor == Game.Color.WHITE || randomPlayer2.playerColor == Game.Color.BLACK);
    }
}
