package pieces;

import elements.*;

import javax.swing.*;

/// @author dana

/**
 * Represents a Pawn piece in the game. The Pawn moves one square forward, but captures diagonally.
 * It also has special moves like en passant and promotion when reaching the opponent's back rank.
 */
public class Pawn extends Piece {
    /// The value of a pawn piece, typically used for material evaluation in chess.
    public final int value = 1;
    /// Indicates if the pawn has moved for the first time, relevant for 2-squares moves.
    public boolean isFirstMove;

    /**
     * Constructs a new {@code Pawn} with the specified color.
     *
     * @param color The color of the Pawn, either white or black.
     */
    public Pawn(Game.Color color) {
        super(color);
        this.isFirstMove = true;
    }

    /**
     * Checks if the Pawn can perform an en passant capture on a given square.
     *
     * @param startSquare The square where the Pawn is located.
     * @param game The current game object, which contains the state of the game.
     * @return true if en passant is possible, false otherwise.
     */
    public boolean enPassant(Square startSquare, Game game) {
        int capturingRank = startSquare.getRank() - 1;
        int capturingFile = startSquare.getFile() - 'a';

        // Get the last move from the game history
        Move lastMove = game.gameHistory.get(game.gameHistory.size() - 1);
        Piece lastPieceMoved = lastMove.getMovedPiece();

        // Check if the last move was made by a pawn of the opposite color
        if (lastPieceMoved instanceof Pawn && lastPieceMoved.color != color) {
            Square capturedStartSquare = lastMove.getStart();
            Square capturedEndSquare = lastMove.getEnd();

            int startSquareRank = capturedStartSquare.getRank() - 1;
            int endSquareRank = capturedEndSquare.getRank() - 1;
            int endSquareFile = capturedEndSquare.getFile() - 'a';

            // Check if the last move was a two-square pawn move
            if (Math.abs(startSquareRank - endSquareRank) == 2) {
                // Check if the moved pawn is adjacent to the capturing pawn
                if (Math.abs(endSquareFile - capturingFile) == 1) {
                    // Check if the capturing pawn is on the correct rank for en passant
                    return (color == Game.Color.WHITE && capturingRank == 4) || (color == Game.Color.BLACK && capturingRank == 3);
                }
            }
        }
        return false;
    }

    /**
     * Promotes the Pawn to another piece (Queen, Rook, Knight, or Bishop) when it reaches the opponent's back rank.
     *
     * @param start The square where the Pawn is currently located.
     * @param end The square the Pawn is moving to.
     * @param game The current game object, which contains the state of the game.
     */
    public void promote(Square start, Square end, Game game) {
        Position position = game.currentPosition;

        if (end.getIsEmpty() && allowedMove(start, end, game)) {
            handlePromotion(start, end, game, position, false);
        } else if (!end.getIsEmpty() && allowedCapture(start, end, game)) {
            handlePromotion(start, end, game, position, true);
        }
    }

    /**
     * Handles the promotion of the Pawn by presenting a dialog to the user to select the new piece.
     *
     * @param start The square where the Pawn is located.
     * @param end The square the Pawn is moving to.
     * @param game The current game object.
     * @param position The current board position.
     * @param isCapture True if the promotion occurs after a capture, false otherwise.
     */
    private void handlePromotion(Square start, Square end, Game game, Position position, boolean isCapture) {
        // Use a modal dialog to block until the user makes a selection
        JFrame frame = new JFrame("Promotion");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(300, 200);
        frame.setLocationRelativeTo(null);

        String[] options = {"Queen", "Rook", "Knight", "Bishop"};
        int choice = JOptionPane.showOptionDialog(
                frame,
                "Promote your pawn to:",
                "Promote Piece",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        Piece promotedPiece = null;

        if (choice >= 0) {
            switch (options[choice]) {
                case "Queen":
                    promotedPiece = new Queen(color);
                    break;
                case "Rook":
                    promotedPiece = new Rook(color);
                    break;
                case "Knight":
                    promotedPiece = new Knight(color);
                    break;
                case "Bishop":
                    promotedPiece = new Bishop(color);
                    break;
            }
        } else {
            JOptionPane.showMessageDialog(frame, "You must select a piece for promotion.");
            // Default to Queen if no valid selection
            promotedPiece = new Queen(color);
        }

        frame.dispose();

        // Update the game state after promotion
        updateGameAfterPromotion(start, end, game, position, promotedPiece, isCapture);
    }

    /**
     * Updates the game state after a Pawn has been promoted to a new piece.
     *
     * @param start The square where the Pawn is located.
     * @param end The square the Pawn is moving to.
     * @param game The current game object.
     * @param position The current board position.
     * @param promotedPiece The piece the Pawn has been promoted to.
     * @param isCapture True if the promotion occurs after a capture, false otherwise.
     */
    private void updateGameAfterPromotion(Square start, Square end, Game game, Position position, Piece promotedPiece, boolean isCapture) {
        int startRank = start.getRank() - 1;
        int startFile = start.getFile() - 'a';
        int endRank = end.getRank() - 1;
        int endFile = end.getFile() - 'a';

        char promotedPieceNotation = getPromotedPieceNotation(promotedPiece);

        // Construct the move notation
        String moveNotation = isCapture
                ? start.getFile() + "x" + end.getFile() + end.getRank() + "=" + promotedPieceNotation
                : end.getFile() + String.valueOf(end.getRank()) + "=" + promotedPieceNotation;

        if (isCapture) {
            if (color == game.getMe().playerColor) {
                game.getMeCapturedPieces().add(position.board[endRank][endFile].getPiece());
            } else {
                game.getOpponentCapturedPieces().add(position.board[endRank][endFile].getPiece());
            }
        }

        // Move the piece to the end Square
        position.board[endRank][endFile].setIsEmpty(false);
        position.board[endRank][endFile].setPiece(promotedPiece);

        // Set the start Square empty
        position.board[startRank][startFile].setIsEmpty(true);
        position.board[startRank][startFile].setPiece(null);

        position.positionNumber++;
        game.halfMoveClock = 0;
        game.currentPosition = position;

        int moveNumber = (int) (Math.ceil((double) game.currentPosition.positionNumber / 2));

        Move newMove = new Move(start, end, moveNumber, this, moveNotation, game.currentPosition);

        // Update the game history
        game.gameHistory.add(newMove);

        game.whiteMoves = !game.whiteMoves;
    }

    /**
     * Returns the notation character for the promoted piece.
     *
     * @param promotedPiece The piece the Pawn has been promoted to.
     * @return The notation character of the promoted piece (e.g., 'Q' for Queen).
     */
    private static char getPromotedPieceNotation(Piece promotedPiece) {
        char promotedPieceNotation;
        if (promotedPiece instanceof Queen) {
            promotedPieceNotation = 'Q';
        } else if (promotedPiece instanceof Rook) {
            promotedPieceNotation = 'R';
        } else if (promotedPiece instanceof Knight) {
            promotedPieceNotation = 'N';
        } else if (promotedPiece instanceof Bishop) {
            promotedPieceNotation = 'B';
        } else {
            throw new IllegalStateException("Unknown promoted piece type.");
        }
        return promotedPieceNotation;
    }

    /**
     * Determines if the pawn is allowed to move from the start square to the end square.
     *
     * @param start the starting square
     * @param end the ending square
     * @param game the current game instance
     * @return true if the move is allowed, false otherwise
     */
    @Override
    public boolean allowedMove(Square start, Square end, Game game) {
        int startRank = start.getRank() - 1;
        int startFile = start.getFile() - 'a';
        int endRank = end.getRank() - 1;
        int endFile = end.getFile() - 'a';

        Position position = game.currentPosition;

        if (!position.board[endRank][endFile].getIsEmpty()) return false;

        if (startFile != endFile) return false;

        if (isFirstMove) {
            if (color == Game.Color.WHITE) {
                if (endRank != startRank + 1 && endRank != startRank + 2) return false;

                boolean emptyPath = true;
                for (int i = startRank + 1; i < endRank; i++) {
                    if (!position.board[i][startFile].getIsEmpty()) {
                        emptyPath = false;
                        break;
                    }
                }
                return emptyPath;
            } else {
                if (endRank != startRank - 1 && endRank != startRank - 2) return false;

                boolean emptyPath = true;
                for (int i = startRank - 1; i > endRank; i--) {
                    if (!position.board[i][startFile].getIsEmpty()) {
                        emptyPath = false;
                        break;
                    }
                }
                return emptyPath;
            }
        } else {
            if (color == Game.Color.WHITE) {
                if (endRank != startRank + 1) return false;

                boolean emptyPath = true;
                for (int i = startRank + 1; i < endRank; i++) {
                    if (!position.board[i][startFile].getIsEmpty()) {
                        emptyPath = false;
                        break;
                    }
                }
                return emptyPath;
            } else {
                if (endRank != startRank - 1) return false;

                boolean emptyPath = true;
                for (int i = startRank - 1; i > endRank; i--) {
                    if (!position.board[i][startFile].getIsEmpty()) {
                        emptyPath = false;
                        break;
                    }
                }
                return emptyPath;
            }
        }
    }

    /**
     * Makes the pawn move from the start square to the end square, handling promotion if needed.
     *
     * @param start the starting square
     * @param end the ending square
     * @param game the current game instance
     */
    @Override
    public void makeMove(Square start, Square end, Game game) {
        if ((color == Game.Color.WHITE && end.getRank() == 8) || (color == Game.Color.BLACK && end.getRank() == 1)) {
            promote(start, end, game);
        } else super.makeMove(start, end, game);
        this.isFirstMove = false;
    }

    /**
     * Determines if the pawn is allowed to capture a piece on the end square.
     * A capture is valid if the pawn moves diagonally to capture an opposing piece, or if en passant is possible.
     *
     * @param start the starting square
     * @param end the ending square
     * @param game the current game instance
     * @return true if the capture is allowed, false otherwise
     */
    @Override
    public boolean allowedCapture(Square start, Square end, Game game) {
        int startRank = start.getRank() - 1;
        int endRank = end.getRank() - 1;
        int endFile = end.getFile() - 'a';

        int rankDifference = Math.abs(end.getRank() - start.getRank());
        int fileDifference = Math.abs(end.getFile() - start.getFile());

        if (color == Game.Color.WHITE) {
            // For pawns, a capture is valid only if it is diagonal by one square
            if (rankDifference == 1 && fileDifference == 1 && endRank > startRank) {
                Position position = game.currentPosition;

                // Check if the target square is empty (potential en passant)
                if (position.board[endRank][endFile].getIsEmpty()) {
                    return enPassant(start, game); // Validate en passant
                }

                // Regular capture: Ensure target piece exists and is of the opposite color
                Piece targetPiece = position.board[endRank][endFile].getPiece();
                return targetPiece != null && targetPiece.color != color;
            }
        } else {
            // For pawns, a capture is valid only if it is diagonal by one square
            if (rankDifference == 1 && fileDifference == 1 && endRank < startRank) {
                Position position = game.currentPosition;

                // Check if the target square is empty (potential en passant)
                if (position.board[endRank][endFile].getIsEmpty()) {
                    return enPassant(start, game); // Validate en passant
                }

                // Regular capture: Ensure target piece exists and is of the opposite color
                Piece targetPiece = position.board[endRank][endFile].getPiece();
                return targetPiece != null && targetPiece.color != color;
            }
        }
        return false;
    }

    /**
     * Makes the pawn capture an opponent's piece, handling en passant if necessary.
     *
     * @param start the starting square
     * @param end the ending square
     * @param game the current game instance
     */
    @Override
    public void makeCapture(Square start, Square end, Game game) {
        if (enPassant(start, game)) {
            Position position = game.currentPosition;
            if (allowedCapture(start, end, game)) {
                Move lastMove = game.gameHistory.get(game.gameHistory.size() - 1);
                Piece lastPieceMoved = lastMove.getMovedPiece();

                int startRank = start.getRank() - 1;
                int startFile = start.getFile() - 'a';
                int endRank = end.getRank() - 1;
                int endFile = end.getFile() - 'a';

                String moveNotation = start.getFile() + "x" + end.getFile() + end.getRank();

                if (color == game.getMe().playerColor) game.getMeCapturedPieces().add(lastPieceMoved);
                else game.getOpponentCapturedPieces().add(lastPieceMoved);

                // Set the last moved piece end square empty
                lastMove.getEnd().setIsEmpty(true);
                lastMove.getEnd().setPiece(null);

                // Set the end square not empty and setting piece
                position.board[endRank][endFile].setIsEmpty(false);
                position.board[endRank][endFile].setPiece(position.board[startRank][startFile].getPiece());

                // Set the start Square empty
                position.board[startRank][startFile].setIsEmpty(true);
                position.board[startRank][startFile].setPiece(null);

                position.positionNumber++;
                game.currentPosition = position;

                Move newMove = new Move(start, end, game.currentPosition.positionNumber, this, moveNotation, game.currentPosition);

                // Update the game history
                game.gameHistory.add(newMove);

                game.whiteMoves = !game.whiteMoves;
            }
        } else {
            if ((color == Game.Color.WHITE && end.getRank() == 8) || (color == Game.Color.BLACK && end.getRank() == 1)) {
                promote(start, end, game);
            } else super.makeCapture(start, end, game);
        }
        this.isFirstMove = false;
    }

    /**
     * Creates a new copy of the current pawn.
     *
     * @return a new Pawn object with the same color and first move status
     */
    @Override
    public Piece copy() {
        Pawn copy =  new Pawn(color);
        copy.isFirstMove = isFirstMove;
        return copy;
    }
}