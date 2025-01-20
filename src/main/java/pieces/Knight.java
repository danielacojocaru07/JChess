package pieces;

import elements.*;

/// @author dana

/**
 * Represents a Knight piece in the game. The Knight moves in an "L" shape: two squares in one direction and then one square perpendicular.
 */
public class Knight extends Piece {
    /// The value of a knight piece, typically used for material evaluation in chess.
    public final int value = 3;

    /**
     * Constructs a new {@code Knight} with a specified color.
     *
     * @param color The color of the Knight, either white or black.
     */
    public Knight(Game.Color color) {
        super(color);
    }

    /**
     * Determines if the Knight is allowed to move from the starting square to the ending square.
     * The Knight moves in an "L" shape: two squares in one direction and then one square perpendicular.
     *
     * @param start The square where the Knight is currently located.
     * @param end The square the Knight is trying to move to.
     * @param game The current game object, which contains the state of the game.
     * @return true if the move is allowed, false otherwise.
     */
    @Override
    public boolean allowedMove(Square start, Square end, Game game) {
        int startRank = start.getRank() - 1;
        int startFile = start.getFile() - 'a';
        int endRank = end.getRank() - 1;
        int endFile = end.getFile() - 'a';

        Position position = game.currentPosition;

        int rankDiff = Math.abs(endRank - startRank);
        int fileDiff = Math.abs(endFile - startFile);

        if (!position.board[endRank][endFile].getIsEmpty()) return false;

        if (rankDiff == 2 && fileDiff == 1) return true;
        else return rankDiff == 1 && fileDiff == 2;
    }

    /**
     * Executes the Knight's move from the starting square to the ending square.
     * The Knight moves in an "L" shape if the move is valid.
     *
     * @param start The square where the Knight is currently located.
     * @param end The square the Knight is moving to.
     * @param game The current game object, which contains the state of the game.
     */
    @Override
    public void makeMove(Square start, Square end, Game game) {
        super.makeMove(start, end, game);
    }

    /**
     * Determines if the Knight is allowed to capture a piece from the starting square to the ending square.
     * The Knight captures in the same "L" shape pattern as it moves.
     *
     * @param start The square where the Knight is currently located.
     * @param end The square the Knight is trying to capture a piece from.
     * @param game The current game object, which contains the state of the game.
     * @return true if the capture is allowed, false otherwise.
     */
    @Override
    public boolean allowedCapture(Square start, Square end, Game game) {
        int startRank = start.getRank() - 1;
        int startFile = start.getFile() - 'a';
        int endRank = end.getRank() - 1;
        int endFile = end.getFile() - 'a';

        Position position = game.currentPosition;

        int rankDiff = Math.abs(endRank - startRank);
        int fileDiff = Math.abs(endFile - startFile);

        if (position.board[endRank][endFile].getIsEmpty() ||
                position.board[endRank][endFile].getPiece().color ==
                        position.board[startRank][startFile].getPiece().color) return false;

        if (rankDiff == 2 && fileDiff == 1) return true;
        else return rankDiff == 1 && fileDiff == 2;
    }

    /**
     * Executes the Knight's capture from the starting square to the ending square.
     * The capture follows the same movement pattern as the Knight's allowed move.
     *
     * @param start The square where the Knight is currently located.
     * @param end The square the Knight is capturing a piece from.
     * @param game The current game object, which contains the state of the game.
     */
    @Override
    public void makeCapture(Square start, Square end, Game game) {
        super.makeCapture(start, end, game);
    }

    /**
     * Creates a new copy of the Knight with the same color.
     *
     * @return A new copy of the Knight piece.
     */
    @Override
    public Piece copy() {
        return new Knight(color);
    }
}