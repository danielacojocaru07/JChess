package pieces;

import elements.*;

/// @author dana

/**
 * Represents a bishop chess piece.
 * This piece can move diagonally on the board and captures pieces of the opposite color in the same way.
 */
public class Bishop extends Piece {
    /// The value of a bishop piece, typically used for material evaluation in chess.
    public final int value = 3;

    /**
     * Constructs a {@code Bishop} with a specified color.
     *
     * @param color The color of the bishop, either WHITE or BLACK.
     */
    public Bishop(Game.Color color) {
        super(color);
    }

    /**
     * Checks if the path between the start and end squares is clear for a bishop's move.
     * A bishop can only move along diagonals, so this method checks if the path between
     * the start and end squares is unblocked by other pieces.
     *
     * @param start The starting square of the move.
     * @param end The ending square of the move.
     * @param game The current game instance to access the board.
     * @return True if the path is clear, false otherwise.
     */
    public boolean checkEmptyPath (Square start, Square end, Game game) {
        int startRank = start.getRank() - 1;
        int startFile = start.getFile() - 'a';
        int endRank = end.getRank() - 1;
        int endFile = end.getFile() - 'a';

        Position position = game.currentPosition;

        if (startRank == endRank || startFile == endFile) {
            return false;
        } else if (endRank > startRank) {
            boolean emptyPath = true;
            if (endFile > startFile) {
                for (int i = startRank + 1, j = startFile + 1; i < endRank && j < endFile; i++, j++) {
                    if (!position.board[i][j].getIsEmpty()) {
                        emptyPath = false;
                        break;
                    }
                }
            } else {
                for (int i = startRank + 1, j = startFile - 1; i < endRank && j > endFile; i++, j--) {
                    if (!position.board[i][j].getIsEmpty()) {
                        emptyPath = false;
                        break;
                    }
                }
            }
            return emptyPath;
        } else {
            boolean emptyPath = true;
            if (endFile < startFile) {
                for (int i = startRank - 1, j = startFile - 1; i > endRank && j > endFile; i--, j--) {
                    if (!position.board[i][j].getIsEmpty()) {
                        emptyPath = false;
                        break;
                    }
                }
            } else {
                for (int i = startRank - 1, j = startFile + 1; i > endRank && j < endFile; i--, j++) {
                    if (!position.board[i][j].getIsEmpty()) {
                        emptyPath = false;
                        break;
                    }
                }
            }
            return emptyPath;
        }
    }

    /**
     * Checks if the bishop can move from the start to the end square based on the game's rules.
     * A bishop can only move diagonally and can only move to an empty square.
     *
     * @param start The starting square of the move.
     * @param end The ending square of the move.
     * @param game The current game instance.
     * @return True if the move is allowed, false otherwise.
     */
    @Override
    public boolean allowedMove(Square start, Square end, Game game) {
        int startRank = start.getRank() - 1;
        int startFile = start.getFile() - 'a';
        int endRank = end.getRank() - 1;
        int endFile = end.getFile() - 'a';

        int rankDiff = Math.abs(endRank - startRank);
        int fileDiff = Math.abs(endFile - startFile);

        if (rankDiff != fileDiff) return false;

        Position position = game.currentPosition;

        if (!position.board[endRank][endFile].getIsEmpty()) return false;

        return checkEmptyPath(start, end, game);
    }

    /**
     * Makes the move from the start square to the end square if it is allowed by the game's rules.
     * This method delegates the actual movement to the parent class method.
     *
     * @param start The starting square of the move.
     * @param end The ending square of the move.
     * @param game The current game instance.
     */
    @Override
    public void makeMove(Square start, Square end, Game game) {
        super.makeMove(start, end, game);
    }

    /**
     * Checks if the bishop can capture an opponent's piece from the start to the end square.
     * A bishop can capture diagonally and only if the destination square contains a piece of the opposite color.
     *
     * @param start The starting square of the capture.
     * @param end The ending square of the capture.
     * @param game The current game instance.
     * @return True if the capture is allowed, false otherwise.
     */
    @Override
    public boolean allowedCapture(Square start, Square end, Game game) {
        int startRank = start.getRank() - 1;
        int startFile = start.getFile() - 'a';
        int endRank = end.getRank() - 1;
        int endFile = end.getFile() - 'a';

        int rankDiff = Math.abs(endRank - startRank);
        int fileDiff = Math.abs(endFile - startFile);

        if (rankDiff != fileDiff) return false;

        Position position = game.currentPosition;

        if (position.board[endRank][endFile].getIsEmpty() ||
                position.board[endRank][endFile].getPiece().color ==
                        position.board[startRank][startFile].getPiece().color) return false;

        return checkEmptyPath(start, end, game);
    }

    /**
     * Makes the capture from the start square to the end square if it is allowed by the game's rules.
     * This method delegates the actual capture to the parent class method.
     *
     * @param start The starting square of the capture.
     * @param end The ending square of the capture.
     * @param game The current game instance.
     */
    @Override
    public void makeCapture(Square start, Square end, Game game) {
        super.makeCapture(start, end, game);
    }

    /**
     * Creates a copy of this bishop piece with the same color.
     *
     * @return A new bishop piece of the same color.
     */
    @Override
    public Piece copy() {
        return new Bishop(color);
    }
}