package elements;

import java.util.Random;

/// @author dana

/**
 * Represents a player in the game, which includes their color option and the color they are assigned to play.
 */
public class Player {

    /**
     * Enum representing the possible color options for a player.
     * WHITE: The player will play as white.
     * BLACK: The player will play as black.
     * RANDOM: The player will be randomly assigned either white or black.
     */
    public enum ColorOption {
        WHITE, BLACK, RANDOM
    }

    /// The color of the player (either WHITE or BLACK).
    public Game.Color playerColor;
    /// The color option chosen for the player (WHITE, BLACK, or RANDOM)
    public final ColorOption colorOption;

    /**
     * Constructs a new {@code Player} based on the provided color option.
     * If the color option is RANDOM, the player is randomly assigned a color.
     *
     * @param colorOption The color option for the player (WHITE, BLACK, or RANDOM).
     */
    public Player(ColorOption colorOption) {
        this.colorOption = colorOption;
        switch (colorOption) {
            case WHITE:
                this.playerColor = Game.Color.WHITE;
                break;
            case BLACK:
                this.playerColor = Game.Color.BLACK;
                break;
            case RANDOM:
                Random random = new Random();
                this.playerColor = random.nextBoolean() ? Game.Color.WHITE : Game.Color.BLACK;
        }
    }

    /**
     * Creates a copy of the current player with the same color and color option.
     *
     * @return A new Player object that is a copy of the current player.
     */
    public Player copy() {
        Player copy = new Player(this.colorOption);
        copy.playerColor = this.playerColor;
        return copy;
    }
}