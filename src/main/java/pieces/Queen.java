package pieces;

import elements.*;

/// @author dana

/**
 * Represents a Queen piece in the game of chess. The Queen can move any number of squares along a rank, file, or diagonal.
 */
public class Queen extends Piece {
    // The value of the Queen piece
    public final int value = 9;

    /**
     * Creates a new {@code Queen} piece with the specified color.
     *
     * @param color the color of the Queen (either white or black)
     */
    public Queen(Game.Color color) {
        super(color);
    }

    /**
     * Checks if the path between the start square and the end square is empty for a Queen's move.
     * A Queen moves any number of squares along a rank, file, or diagonal, but cannot jump over other pieces.
     *
     * @param start the starting square
     * @param end the ending square
     * @param game the current game instance
     * @return true if the path is empty and the Queen can move, false otherwise
     */
    public boolean checkEmptyPath(Square start, Square end, Game game) {
        int startRank = start.getRank() - 1;
        int startFile = start.getFile() - 'a';
        int endRank = end.getRank() - 1;
        int endFile = end.getFile() - 'a';

        Position position = game.currentPosition;

        int rankDiff = Math.abs(endRank - startRank);
        int fileDiff = Math.abs(endFile - startFile);

        if (rankDiff == 0) {
            boolean emptyPath = true;
            if (startFile < endFile) {
                for (int i = startFile + 1; i < endFile; i++) {
                    if (!position.board[startRank][i].getIsEmpty()) {
                        emptyPath = false;
                        break;
                    }
                }
            } else {
                for (int i = startFile - 1; i > endFile; i--) {
                    if (!position.board[startRank][i].getIsEmpty()) {
                        emptyPath = false;
                        break;
                    }
                }
            }
            return emptyPath;
        } else if (fileDiff == 0) {
            boolean emptyPath = true;
            if (startRank < endRank) {
                for (int i = startRank + 1; i < endRank; i++) {
                    if (!position.board[i][startFile].getIsEmpty()) {
                        emptyPath = false;
                        break;
                    }
                }
            } else {
                for (int i = startRank - 1; i > endRank; i--) {
                    if (!position.board[i][startFile].getIsEmpty()) {
                        emptyPath = false;
                        break;
                    }
                }
            }
            return emptyPath;
        } else if (rankDiff == fileDiff) {
            boolean emptyPath = true;
            if (endRank > startRank) {
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
            } else {
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
            }
            return emptyPath;
        }
        return false;
    }

    /**
     * Determines if the Queen is allowed to move from the start square to the end square.
     * The Queen can move along a rank, file, or diagonal, provided the path is not blocked.
     *
     * @param start the starting square
     * @param end the ending square
     * @param game the current game instance
     * @return true if the move is allowed, false otherwise
     */
    @Override
    public boolean allowedMove(Square start, Square end, Game game) {
        int endRank = end.getRank() - 1;
        int endFile = end.getFile() - 'a';

        Position position = game.currentPosition;

        if (!position.board[endRank][endFile].getIsEmpty()) return false;

        return checkEmptyPath(start, end, game);
    }

    /**
     * Executes the move for the Queen from the start square to the end square.
     *
     * @param start the starting square
     * @param end the ending square
     * @param game the current game instance
     */
    @Override
    public void makeMove(Square start, Square end, Game game) {
        super.makeMove(start, end, game);
    }

    /**
     * Determines if the Queen is allowed to capture an opponent's piece on the end square.
     * The Queen can capture an opposing piece by moving along a rank, file, or diagonal, provided the path is not blocked.
     *
     * @param start the starting square
     * @param end the ending square
     * @param game the current game instance
     * @return true if the capture is allowed, false otherwise
     */
    @Override
    public boolean allowedCapture(Square start, Square end, Game game) {
        int startRank = start.getRank() - 1;
        int startFile = start.getFile() - 'a';
        int endRank = end.getRank() - 1;
        int endFile = end.getFile() - 'a';

        Position position = game.currentPosition;

        if (position.board[endRank][endFile].getIsEmpty() ||
                position.board[endRank][endFile].getPiece().color ==
                        position.board[startRank][startFile].getPiece().color) return false;

        return checkEmptyPath(start, end, game);
    }

    /**
     * Executes the capture for the Queen, removing the captured piece from the game.
     *
     * @param start the starting square
     * @param end the ending square
     * @param game the current game instance
     */
    @Override
    public void makeCapture(Square start, Square end, Game game) {
        super.makeCapture(start, end, game);
    }

    /**
     * Creates and returns a new copy of the Queen with the same color.
     *
     * @return a new Queen piece with the same color
     */
    @Override
    public Piece copy() {
        return new Queen(color);
    }
}