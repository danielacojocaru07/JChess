package pieces;

import elements.*;

/// @author dana

/**
 * Represents a king chess piece. The king can move one square in any direction,
 * and it can also perform castling under certain conditions.
 */
public class King extends Piece {
    /// The value of a king piece, typically used for material evaluation in chess.
    public final int value = 20;
    /// Indicates if the king has moved for the first time, relevant for castling.
    public boolean isFirstMove;

    /**
     * Constructs a {@code King} with a specified color.
     *
     * @param color The color of the king, either WHITE or BLACK.
     */
    public King(Game.Color color) {
        super(color);
        isFirstMove = true;
    }

    /**
     * Checks if castling on the king-side is possible for the king.
     * The conditions for castling include: the king has not moved, the rook has not moved,
     * the path between the king and the rook are clear, and the king is not in check.
     *
     * @param start The starting square of the move.
     * @param end The ending square of the move.
     * @param game The current game instance.
     * @return True if the king can castle on the king-side, false otherwise.
     */
    public boolean castleKingSide(Square start, Square end, Game game) {
        int startRank = start.getRank() - 1;
        int startFile = start.getFile() - 'a';
        int endFile = end.getFile() - 'a';

        Position position = game.currentPosition;

        // Basic validation
        if (!isFirstMove || Math.abs(endFile - startFile) != 2) return false;

        // Check if the path is clear
        for (int file = startFile + 1; file < 7; file++) {
            if (!position.board[startRank][file].getIsEmpty()) return false;
        }

        // Validate the rook
        Piece rook = position.board[startRank][7].getPiece();
        if (!(rook instanceof Rook) || !((Rook) rook).isFirstMove) return false;

        // Check the king does not move through or into check
        if (wouldBeInCheck(startRank, startFile + 1, game)) return false;  // Check if moving to the intermediate position (file 5) puts the king in check
        return !wouldBeInCheck(startRank, startFile + 2, game);  // Check if moving to the final position (file 6) puts the king in check
    }

    /**
     * Performs castling on the king-side by moving the king and the rook to their new positions.
     *
     * @param start The starting square of the move.
     * @param end The ending square of the move.
     * @param game The current game instance.
     */
    public void makeCastleKingSide(Square start, Square end, Game game) {
        int startRank = start.getRank() - 1;

        Position position = game.currentPosition;

        // Move the king
        position.board[startRank][6].setPiece(this);
        position.board[startRank][6].setIsEmpty(false);
        position.board[startRank][4].setPiece(null);
        position.board[startRank][4].setIsEmpty(true);

        // Move the rook
        Piece rook = position.board[startRank][7].getPiece();
        position.board[startRank][5].setPiece(rook);
        position.board[startRank][5].setIsEmpty(false);
        position.board[startRank][7].setPiece(null);
        position.board[startRank][7].setIsEmpty(true);

        // Update state
        isFirstMove = false;
        if (rook instanceof Rook) ((Rook) rook).isFirstMove = false;

        position.positionNumber++;
        game.halfMoveClock++;
        game.currentPosition = position;

        int moveNumber = (int) (Math.ceil((double) game.currentPosition.positionNumber / 2));

        String moveNotation = "O-O";
        if (game.isCheckmateForWhite() || game.isCheckmateForBlack()) moveNotation += "#";
        else if (game.isCheckForWhite() || game.isCheckForBlack()) moveNotation += "+";

        Move newMove = new Move(start, end, moveNumber, this, moveNotation, game.currentPosition);

        // Update the game history
        game.gameHistory.add(newMove);

        game.whiteMoves = !game.whiteMoves;
    }

    /**
     * Checks if castling on the queen-side is possible for the king.
     * The conditions for castling include: the king has not moved, the rook has not moved,
     * the path between the king and the rook are clear, and the king is not in check.
     *
     * @param start The starting square of the move.
     * @param end The ending square of the move.
     * @param game The current game instance.
     * @return True if the king can castle on the queen-side, false otherwise.
     */
    public boolean castleQueenSide(Square start, Square end, Game game) {
        int startRank = start.getRank() - 1;
        int startFile = start.getFile() - 'a';
        int endFile = end.getFile() - 'a';

        Position position = game.currentPosition;

        // Basic validation
        if (!isFirstMove || Math.abs(endFile - startFile) != 2) return false;

        // Validate the rook
        Piece rook = position.board[startRank][0].getPiece();
        if (!(rook instanceof Rook) || !((Rook) rook).isFirstMove) return false;

        // Check if the path is clear
        for (int file = startFile - 1; file > 0; file--) {
            if (!position.board[startRank][file].getIsEmpty()) return false;
        }

        // Check the king does not move through or into check
        if (wouldBeInCheck(startRank, startFile - 1, game)) return false;  // Check if moving to the intermediate position (file 5) puts the king in check
        return !wouldBeInCheck(startRank, startFile - 2, game);  // Check if moving to the final position (file 6) puts the king in check

    }

    /**
     * Performs castling on the queen-side by moving the king and the rook to their new positions.
     *
     * @param start The starting square of the move.
     * @param end The ending square of the move.
     * @param game The current game instance.
     */
    public void makeCastleQueenSide(Square start, Square end, Game game) {
        int startRank = start.getRank() - 1;
        Position position = game.currentPosition;

        // Move the king
        position.board[startRank][2].setPiece(this);
        position.board[startRank][2].setIsEmpty(false);
        position.board[startRank][4].setPiece(null);
        position.board[startRank][4].setIsEmpty(true);

        // Move the rook
        Piece rook = position.board[startRank][0].getPiece();
        position.board[startRank][3].setPiece(rook);
        position.board[startRank][3].setIsEmpty(false);
        position.board[startRank][0].setPiece(null);
        position.board[startRank][0].setIsEmpty(true);

        // Update state
        isFirstMove = false;
        if (rook instanceof Rook) ((Rook) rook).isFirstMove = false;

        position.positionNumber++;
        game.halfMoveClock++;
        game.currentPosition = position;

        int moveNumber = (int) (Math.ceil((double) game.currentPosition.positionNumber / 2));

        String moveNotation = "O-O-O";
        if (game.isCheckmateForWhite() || game.isCheckmateForBlack()) moveNotation += "#";
        else if (game.isCheckForWhite() || game.isCheckForBlack()) moveNotation += "+";

        Move newMove = new Move(start, end, moveNumber, this, moveNotation, game.currentPosition);

        // Update the game history
        game.gameHistory.add(newMove);

        game.whiteMoves = !game.whiteMoves;
    }

    /**
     * Determines if the king would be in check if it moved to a given position.
     *
     * @param rank The rank of the position to check.
     * @param file The file of the position to check.
     * @param game The current game instance.
     * @return True if the king would be in check, false otherwise.
     */
    private boolean wouldBeInCheck(int rank, int file, Game game) {
        Square tempSquare = game.currentPosition.board[rank][file];
        Piece originalPiece = tempSquare.getPiece();
        boolean originalEmptyState = tempSquare.getIsEmpty();

        tempSquare.setIsEmpty(false);
        tempSquare.setPiece(this);

        boolean inCheck = (color == Game.Color.WHITE && game.isCheckForWhite()) ||
                (color == Game.Color.BLACK && game.isCheckForBlack());

        tempSquare.setIsEmpty(originalEmptyState);
        tempSquare.setPiece(originalPiece);

        return inCheck;
    }

    /**
     * Determines if the King is allowed to move from the starting square to the ending square.
     * The King can move one square in any direction, or castling can occur if conditions are met.
     *
     * @param start The square where the King is currently located.
     * @param end The square the King is trying to move to.
     * @param game The current game object, which contains the state of the game.
     * @return true if the move is allowed, false otherwise.
     */
    @Override
    public boolean allowedMove(Square start, Square end, Game game) {
        int startRank = start.getRank() - 1;
        int startFile = start.getFile() - 'a';
        int endRank = end.getRank() - 1;
        int endFile = end.getFile() - 'a';

        int rankDiff = Math.abs(endRank - startRank);
        int fileDiff = Math.abs(endFile - startFile);

        Position position = game.currentPosition;

        if (!position.board[endRank][endFile].getIsEmpty()) return false;

        if (rankDiff == 0 && fileDiff == 2) {
            return castleKingSide(start, end, game) || castleQueenSide(start, end, game);
        }
        return (rankDiff <= 1 && fileDiff <= 1);
    }

    /**
     * Executes the King's move from the starting square to the ending square.
     * If the move involves castling, the appropriate castling move is made.
     *
     * @param start The square where the King is currently located.
     * @param end The square the King is moving to.
     * @param game The current game object, which contains the state of the game.
     */
    @Override
    public void makeMove(Square start, Square end, Game game) {
        int startFile = start.getFile() - 'a';
        int endFile = end.getFile() - 'a';

        if (Math.abs(endFile - startFile) == 2) {
            if (castleKingSide(start, end, game)) makeCastleKingSide(start, end, game);
            else if (castleQueenSide(start, end, game)) makeCastleQueenSide(start, end, game);
        } else {
            super.makeMove(start, end, game);
        }
        isFirstMove = false;
    }

    /**
     * Determines if the King is allowed to capture a piece from the starting square to the ending square.
     * The King can capture a piece in any direction, as long as it is one square away.
     *
     * @param start The square where the King is currently located.
     * @param end The square the King is trying to capture a piece from.
     * @param game The current game object, which contains the state of the game.
     * @return true if the capture is allowed, false otherwise.
     */
    @Override
    public boolean allowedCapture(Square start, Square end, Game game) {
        int startRank = start.getRank() - 1;
        int startFile = start.getFile() - 'a';
        int endRank = end.getRank() - 1;
        int endFile = end.getFile() - 'a';

        int rankDiff = Math.abs(endRank - startRank);
        int fileDiff = Math.abs(endFile - startFile);

        Position position = game.currentPosition;

        if (position.board[endRank][endFile].getIsEmpty() ||
                position.board[endRank][endFile].getPiece().color ==
                        position.board[startRank][startFile].getPiece().color) return false;

        return (rankDiff == 0 && fileDiff == 1) || (rankDiff == 1 && fileDiff == 0) || (rankDiff == 1 && fileDiff == 1);
    }

    /**
     * Executes the King's capture from the starting square to the ending square.
     * After the capture, the King's first move state is updated.
     *
     * @param start The square where the King is currently located.
     * @param end The square the King is capturing a piece from.
     * @param game The current game object, which contains the state of the game.
     */
    @Override
    public void makeCapture(Square start, Square end, Game game) {
        super.makeCapture(start, end, game);
        isFirstMove = false;
    }

    /**
     * Creates a new copy of the King, including its first move state.
     *
     * @return A new copy of the King piece.
     */
    @Override
    public Piece copy() {
        King copy =  new King(color);
        copy.isFirstMove = isFirstMove;
        return copy;
    }
}