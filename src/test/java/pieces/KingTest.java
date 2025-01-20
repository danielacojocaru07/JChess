package pieces;

import elements.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class KingTest {
    private King whiteKing;

    @BeforeEach
    void setUp() {
        whiteKing = new King(Game.Color.WHITE);  // Create white King
    }

    @Test
    void testCopy() {
        // Test: Verify copy functionality of the King
        King copiedKing = (King) whiteKing.copy();
        assertNotNull(copiedKing, "Copy of the King should not be null.");
        assertEquals(copiedKing.color, whiteKing.color, "Copied King should have the same color.");
        assertEquals(copiedKing.isFirstMove, whiteKing.isFirstMove, "Copied King should have the same first move status.");
    }
}
