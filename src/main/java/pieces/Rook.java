package pieces;

import elements.*;

/// @author dana

/**
 * Represents a Rook piece in the game of chess. The Rook can move any number of squares along a rank or file.
 * It cannot jump over other pieces.
 */
public class Rook extends Piece{
    /// The value of the Rook piece
    public final int value = 5;
    /// Flag to check if the Rook has made its first move
    public boolean isFirstMove;

    /**
     * Creates a new {@code Rook} piece with the specified color.
     *
     * @param color the color of the Rook (either white or black)
     */
    public Rook(Game.Color color) {
        super(color);
        isFirstMove = true;
    }

    /**
     * Checks if the path between the start square and the end square is empty for a Rook's move.
     * A Rook moves any number of squares along a rank or file but cannot jump over other pieces.
     *
     * @param start the starting square
     * @param end the ending square
     * @param game the current game instance
     * @return true if the path is empty and the Rook can move, false otherwise
     */
    public boolean checkEmptyPath(Square start, Square end, Game game) {
        int startRank = start.getRank() - 1;
        int startFile = start.getFile() - 'a';
        int endRank = end.getRank() - 1;
        int endFile = end.getFile() - 'a';

        Position position = game.currentPosition;

        if (startRank != endRank && startFile != endFile) {
            return false;
        } else if (startRank == endRank) {
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
        } else {
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
        }
    }

    /**
     * Determines if the Rook is allowed to move from the start square to the end square.
     * The Rook can move along a rank or file, provided the path is not blocked.
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
     * Executes the move for the Rook from the start square to the end square.
     * The Rook's first move flag is set to false after the move is made.
     *
     * @param start the starting square
     * @param end the ending square
     * @param game the current game instance
     */
    @Override
    public void makeMove(Square start, Square end, Game game) {
        super.makeMove(start, end, game);
        isFirstMove = false;
    }

    /**
     * Determines if the Rook is allowed to capture an opponent's piece on the end square.
     * The Rook can capture an opposing piece by moving along a rank or file, provided the path is not blocked.
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
     * Executes the capture for the Rook, removing the captured piece from the game.
     * The Rook's first move flag is set to false after the capture is made.
     *
     * @param start the starting square
     * @param end the ending square
     * @param game the current game instance
     */
    @Override
    public void makeCapture(Square start, Square end, Game game) {
        super.makeCapture(start, end, game);
        isFirstMove = false;
    }

    /**
     * Creates and returns a new copy of the Rook with the same color and first move status.
     *
     * @return a new Rook piece with the same color and first move status
     */
    @Override
    public Piece copy() {
        Rook copy =  new Rook(color);
        copy.isFirstMove = isFirstMove;
        return copy;
    }
}