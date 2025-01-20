package elements;

import pieces.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/// @author dana

/**
 * Represents a chess game, including the board position, move history,
 * and the current state of the game.
 */
public class Game {

    /**
     * Enum representing the colors of the chess pieces.
     */
    public enum Color {
        WHITE, BLACK
    }

    /// List of moves played during the game.
    public List<Move> gameHistory;
    /// Current position of the chess pieces on the board.
    public Position currentPosition;
    /// Indicates whether it's White's turn to move.
    public boolean whiteMoves;
    /// Counter for half moves since the last capture or pawn advance.
    public int halfMoveClock;
    /// The player representing the user.
    private Player me;
    /// The opponent player.
    private Player opponent;
    /// List of pieces captured by the user.
    private List<Piece> meCapturedPieces;
    /// List of pieces captured by the opponent.
    private List<Piece> opponentCapturedPieces;

    /**
     * Constructs a new {@code Game} instance, initializing players and the board position.
     *
     * @param colorOption The color chosen by the user (White or Black).
     */
    public Game(Player.ColorOption colorOption) {
        this.gameHistory = new ArrayList<>();
        this.currentPosition = new Position();
        this.whiteMoves = true;
        this.halfMoveClock = 0;
        this.me = new Player(colorOption);
        this.opponent = new Player(me.playerColor == Color.WHITE ? Player.ColorOption.BLACK : Player.ColorOption.WHITE);
        this.meCapturedPieces = new ArrayList<>();
        this.opponentCapturedPieces = new ArrayList<>();
    }

    /**
     * Returns the user player.
     *
     * @return The user player.
     */
    public Player getMe() {
        return me;
    }

    /**
     * Returns the list of pieces captured by the user.
     *
     * @return List of captured pieces.
     */
    public List<Piece> getMeCapturedPieces() {
        return meCapturedPieces;
    }

    /**
     * Returns the list of pieces captured by the opponent.
     *
     * @return List of captured pieces.
     */
    public List<Piece> getOpponentCapturedPieces() {
        return opponentCapturedPieces;
    }

    /**
     * Displays the current game state, including the last move and check status.
     */
    public void display() {
        System.out.println(gameHistory.get(gameHistory.size() - 1).toString(this));
        System.out.println("Is check for white: " + isCheckForWhite());
        System.out.println("Is check for black: " + isCheckForBlack());
    }

    /**
     * Checks if White is in check.
     *
     * @return True if White is in check, false otherwise.
     */
    public boolean isCheckForWhite() {
        return isCheck(Color.WHITE);
    }

    /**
     * Checks if Black is in check.
     *
     * @return True if Black is in check, false otherwise.
     */
    public boolean isCheckForBlack() {
        return isCheck(Color.BLACK);
    }

    /**
     * Checks if a player is in check.
     *
     * @param color The color of the king.
     * @return True if the king is in check, false otherwise.
     */
    private boolean isCheck(Color color) {
        // Find the king's square
        Square kingSquare = findKingSquare(color);
        if (kingSquare == null) {
            throw new IllegalStateException("The king is missing from the board!");
        }

        // Collect all squares containing opponent pieces
        List<Square> opponentPieceSquares = getPiecesOfColor(color == Color.WHITE ? Color.BLACK : Color.WHITE);

        // Check if any opponent piece can capture the king
        for (Square opponentSquare : opponentPieceSquares) {
            Piece opponentPiece = opponentSquare.getPiece();
            if (opponentPiece != null && opponentPiece.allowedCapture(opponentSquare, kingSquare, this)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if White is in checkmate.
     *
     * @return {@code true} if White is in checkmate, {@code false} otherwise.
     */
    public boolean isCheckmateForWhite() {
        return isCheckmate(Color.WHITE);
    }

    /**
     * Checks if Black is in checkmate.
     *
     * @return {@code true} if Black is in checkmate, {@code false} otherwise.
     */
    public boolean isCheckmateForBlack() {
        return isCheckmate(Color.BLACK);
    }

    /**
     * Determines if a move is possible to escape a check situation.
     *
     * @param color the color of the player to check for possible moves.
     * @return {@code true} if no valid move exists to escape check, {@code false} otherwise.
     */
    public boolean possibleMoveFromCheck(Color color) {
        List<Square> mePieceSquares = getPiecesOfColor(color);

        for (Square startSquare : mePieceSquares) {
            Piece piece = startSquare.getPiece();
            if (piece == null) continue;

            for (int rank = 0; rank < 8; rank++) {
                for (int file = 0; file < 8; file++) {
                    Square endSquare = currentPosition.board[rank][file];
                    if (piece.allowedMove(startSquare, endSquare, this) && piece.stopCheck(startSquare, endSquare, this)) {
                        return false;
                    } else if (piece.allowedCapture(startSquare, endSquare, this) && piece.stopCheck(startSquare, endSquare, this)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Checks if a player is in checkmate.
     *
     * @param color the color of the player to check for checkmate.
     * @return {@code true} if the player is in checkmate, {@code false} otherwise.
     */
    public boolean isCheckmate(Color color) {
        if (isCheck(color)) return possibleMoveFromCheck(color);
        return false;
    }

    /**
     * Checks if White is in stalemate.
     *
     * @return {@code true} if White is in stalemate, {@code false} otherwise.
     */
    public boolean isStalemateForWhite() {
        return isStalemate(Color.WHITE);
    } // White can't move nor is in check

    /**
     * Checks if Black is in stalemate.
     *
     * @return {@code true} if Black is in stalemate, {@code false} otherwise.
     */
    public boolean isStalemateForBlack() {
        return isStalemate(Color.BLACK);
    } // Black can't move nor is in check

    /**
     * Checks if a player is in stalemate.
     *
     * @param color the color of the player to check for stalemate.
     * @return {@code true} if the player is in stalemate, {@code false} otherwise.
     */
    public boolean isStalemate(Color color) {
        if (!isCheck(color)) possibleMoveFromCheck(color);
        return false;
    }

    /**
     * Checks if there is insufficient material to continue the game.
     *
     * @param color the color of the player to check for sufficient material.
     * @return {@code true} if there is insufficient material, {@code false} otherwise.
     */
    public boolean insufficientMaterial(Color color) {
        List<Square> myPiecesSquares = getPiecesOfColor(color);
        List<Square> opponentPiecesSquares = getPiecesOfColor(color == Color.WHITE ? Color.BLACK : Color.WHITE);

        int myPieceCount = myPiecesSquares.size();
        int opponentPieceCount = opponentPiecesSquares.size();

        if (myPieceCount == 1 && opponentPieceCount == 1) {
            return true;
        }

        boolean opponentHasOnlyKingAndKnight = opponentPieceCount == 2 &&
                opponentPiecesSquares.stream().anyMatch(square -> square.getPiece() instanceof Knight);
        boolean opponentHasOnlyKingAndBishop = opponentPieceCount == 2 &&
                opponentPiecesSquares.stream().anyMatch(square -> square.getPiece() instanceof Bishop);

        return (opponentHasOnlyKingAndKnight || opponentHasOnlyKingAndBishop) && myPieceCount == 1;
    }

    /**
     * Checks if the 50-move rule has been reached.
     *
     * @return {@code true} if the half-move clock is 50 or more, {@code false} otherwise.
     */
    public boolean rule50Moves() {
        return halfMoveClock >= 50;
    }

    /**
     * Checks if the game is a draw due to stalemate, insufficient material, or the 50-move rule.
     *
     * @return {@code true} if the game is a draw, {@code false} otherwise.
     */
    public boolean isDraw() {
        return (isStalemateForWhite() ||
                isStalemateForBlack() ||
                insufficientMaterial(Color.WHITE) ||
                insufficientMaterial(Color.BLACK) ||
                rule50Moves()
        );
    }

    /**
     * Retrieves all squares containing pieces of the specified color.
     *
     * @param color the color of the pieces to find.
     * @return a list of squares containing pieces of the specified color.
     */
    public List<Square> getPiecesOfColor(Color color) {
        Position position = currentPosition;
        List<Square> pieceSquares = new ArrayList<>();

        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                Square square = position.board[rank][file];
                Piece piece = square.getPiece();
                if (piece != null && piece.color == color) {
                    pieceSquares.add(square);
                }
            }
        }
        return pieceSquares;
    }

    /**
     * Finds the square containing the king of the specified color.
     *
     * @param kingColor the color of the king to find.
     * @return the square containing the king, or {@code null} if not found.
     */
    private Square findKingSquare(Color kingColor) {
        Position position = currentPosition;

        // Iterate through the board to find the king
        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                Piece piece = position.board[rank][file].getPiece();
                if (piece instanceof King && piece.color == kingColor) {
                    return position.board[rank][file];
                }
            }
        }
        return null;
    }

    /**
     * Generates the FEN (Forsyth-Edwards Notation) string representing the current state of the game.
     *
     * @return the FEN string representing the current game state.
     */
    public String FENNotation() {
        if (gameHistory.isEmpty()) {
            return "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        } else {
            StringBuilder fenNotation = new StringBuilder();
            for (int rank = 7; rank >= 0; rank--) {
                int emptySquareCount = 0;
                for (int file = 0; file < 8; file++) {
                    Square square = currentPosition.board[rank][file];
                    if (square.getIsEmpty()) {
                        emptySquareCount++;
                    } else {
                        if (emptySquareCount != 0) fenNotation.append(emptySquareCount);
                        emptySquareCount = 0;
                        String piece = square.getPiece().toString();
                        fenNotation.append(piece);
                    }
                }
                if (emptySquareCount != 0) fenNotation.append(emptySquareCount);
                if (rank != 0) fenNotation.append("/");
                else fenNotation.append(" ");
            }

            if (whiteMoves) fenNotation.append("w ");
            else fenNotation.append("b ");

            Piece whiteKing = Objects.requireNonNull(findKingSquare(Color.WHITE)).getPiece();
            Piece blackKing = Objects.requireNonNull(findKingSquare(Color.BLACK)).getPiece();

            if (((King) whiteKing).isFirstMove) {
                Piece rookKingSide = currentPosition.board[0][7].getPiece();
                Piece rookQueenSide = currentPosition.board[0][0].getPiece();
                if (rookKingSide instanceof Rook && ((Rook) rookKingSide).isFirstMove) fenNotation.append("K");
                if (rookQueenSide instanceof Rook && ((Rook) rookQueenSide).isFirstMove) fenNotation.append("Q");
            }
            if (((King) blackKing).isFirstMove) {
                Piece rookKingSide = currentPosition.board[7][7].getPiece();
                Piece rookQueenSide = currentPosition.board[7][0].getPiece();
                if (rookKingSide instanceof Rook && ((Rook) rookKingSide).isFirstMove) fenNotation.append("k");
                if (rookQueenSide instanceof Rook && ((Rook) rookQueenSide).isFirstMove) fenNotation.append("q");
            }

            if (fenNotation.charAt(fenNotation.length() - 1) == ' ') fenNotation.append("-");
            fenNotation.append(" ");

            Move lastMove = gameHistory.get(gameHistory.size() - 1);
            Piece movedPiece = lastMove.getMovedPiece();
            if (movedPiece instanceof Pawn) {
                Square startSquare = lastMove.getStart();
                Square endSquare = lastMove.getEnd();

                int direction = movedPiece.color == Color.WHITE ? -1 : 1;

                int endRank = endSquare.getRank() - 1;
                int endFile = endSquare.getFile() - 'a';

                Square targetSquare = currentPosition.board[endRank + direction][endFile];

                if (Math.abs(startSquare.getRank() - endSquare.getRank()) == 2) fenNotation.append(targetSquare.toString()).append(" ");
            } else fenNotation.append("- ");

            fenNotation.append(halfMoveClock).append(" ");

            fenNotation.append(gameHistory.isEmpty() ? 1 : gameHistory.get(gameHistory.size() - 1).getMoveNumber());

            return fenNotation.toString();
        }
    }

    /**
     * Creates a deep copy of the current game state.
     *
     * @return a new {@code Game} object that is a deep copy of the current game state.
     */
    public Game copy() {
        Game copy = new Game(me.playerColor == Color.WHITE ? Player.ColorOption.WHITE : Player.ColorOption.BLACK);

        copy.currentPosition = this.currentPosition.copy();

        copy.gameHistory = new ArrayList<>();
        for (Move move : this.gameHistory) {
            copy.gameHistory.add(move.copy());
        }

        copy.meCapturedPieces = new ArrayList<>();
        for (Piece piece : this.meCapturedPieces) {
            copy.meCapturedPieces.add(piece.copy());
        }

        copy.opponentCapturedPieces = new ArrayList<>();
        for (Piece piece : this.opponentCapturedPieces) {
            copy.opponentCapturedPieces.add(piece.copy());
        }

        copy.me = this.me.copy();
        copy.opponent = this.opponent.copy();
        copy.whiteMoves = this.whiteMoves;

        return copy;
    }

    /**
     * Destroys the current game by clearing all game-related data.
     */
    public void destroy() {
        if (gameHistory != null) {
            gameHistory.clear();
        }

        if (meCapturedPieces != null) {
            meCapturedPieces.clear();
        }

        if (opponentCapturedPieces != null) {
            opponentCapturedPieces.clear();
        }

        currentPosition = null;
        whiteMoves = true;
        me = null;
        opponent = null;
    }
}