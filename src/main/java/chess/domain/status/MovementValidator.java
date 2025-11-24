package chess.domain.status;

import static chess.common.message.ErrorMessage.CASTLING_IN_CHECK;
import static chess.common.message.ErrorMessage.CASTLING_KING_MOVED;
import static chess.common.message.ErrorMessage.CASTLING_PATH_ATTACKED;
import static chess.common.message.ErrorMessage.CASTLING_PATH_BLOCKED;
import static chess.common.message.ErrorMessage.CASTLING_ROOK_MOVED;
import static chess.common.message.ErrorMessage.CASTLING_ROOK_NOT_FOUND;
import static chess.common.message.ErrorMessage.PIECE_NOT_FOUND;
import static chess.common.message.ErrorMessage.RULE_FRIENDLY_FIRE;
import static chess.common.message.ErrorMessage.RULE_INVALID_PIECE_MOVE;
import static chess.common.message.ErrorMessage.RULE_KING_IN_CHECK_AFTER_MOVE;
import static chess.common.message.ErrorMessage.RULE_PATH_BLOCKED;
import static chess.common.message.ErrorMessage.RULE_SAME_POSITION_MOVE;
import static chess.common.message.ErrorMessage.RULE_WRONG_TURN_PIECE;

import chess.common.exception.IllegalMoveException;
import chess.common.exception.PieceNotFoundException;
import chess.common.exception.RuleViolationException;
import chess.domain.board.Board;
import chess.domain.board.Position;
import chess.domain.piece.Color;
import chess.domain.piece.Piece;
import chess.domain.piece.Type;
import java.util.stream.IntStream;

public class MovementValidator {

    private final CheckDetector checkDetector;

    public MovementValidator(CheckDetector checkDetector) {
        this.checkDetector = checkDetector;
    }

    public void validate(Position from, Position to, Board board, Color currentTurn) {
        Piece piece = board.getPiece(from);

        if (piece == null) {
            throw new PieceNotFoundException(PIECE_NOT_FOUND.getMessage());
        }

        if (piece.getColor() != currentTurn) {
            throw new RuleViolationException(RULE_WRONG_TURN_PIECE.getMessage());
        }

        if (from.equals(to)) {
            throw new RuleViolationException(RULE_SAME_POSITION_MOVE.getMessage());
        }

        Piece target = board.getPiece(to);
        if (target != null && target.getColor() == piece.getColor()) {
            throw new RuleViolationException(RULE_FRIENDLY_FIRE.getMessage());
        }

        if (!piece.isMoveValid(from, to, board)) {
            throw new RuleViolationException(RULE_INVALID_PIECE_MOVE.getMessage());
        }

        if (isCastling(piece, from, to)) {
            validateCastling(from, to, board, piece);
        }

        if (piece.getType() != Type.KNIGHT && board.hasObstacleInPath(from, to)) {
            throw new RuleViolationException(RULE_PATH_BLOCKED.getMessage());
        }

        if (isKingInCheckAfterMove(from, to, board, currentTurn)) {
            throw new IllegalMoveException(RULE_KING_IN_CHECK_AFTER_MOVE.getMessage());
        }
    }

    private boolean isCastling(Piece piece, Position from, Position to) {
        int dx = Math.abs(from.x() - to.x());
        int dy = Math.abs(from.y() - to.y());
        return piece.getType() == Type.KING && dx == 2 && dy == 0;
    }

    private void validateCastling(Position from, Position to, Board board, Piece king) {
        if (king.isMoved()) {
            throw new IllegalMoveException(CASTLING_KING_MOVED.getMessage());
        }

        if (checkDetector.isCheck(board, king.getColor())) {
            throw new IllegalMoveException(CASTLING_IN_CHECK.getMessage());
        }

        int direction;
        Position rookPosition;
        if (to.x() - from.x() > 0) {
            direction = 1;
            rookPosition = Position.of(7, from.y());
        } else {
            direction = -1;
            rookPosition = Position.of(0, from.y());
        }

        Piece rook = board.getPiece(rookPosition);
        if (rook == null || rook.getType() != Type.ROOK || rook.getColor() != king.getColor()) {
            throw new IllegalMoveException(CASTLING_ROOK_NOT_FOUND.getMessage());
        }

        if (rook.isMoved()) {
            throw new IllegalMoveException(CASTLING_ROOK_MOVED.getMessage());
        }

        if (board.hasObstacleInPath(from, rookPosition)) {
            throw new IllegalMoveException(CASTLING_PATH_BLOCKED.getMessage());
        }

        Position nextSquare = Position.of(from.x() + direction, from.y());
        Color opponentColor = king.getColor().opposite();

        if (checkDetector.isSquareAttacked(board, nextSquare, opponentColor)) {
            throw new IllegalMoveException(CASTLING_PATH_ATTACKED.getMessage());
        }

        if (checkDetector.isSquareAttacked(board, to, opponentColor)) {
            throw new IllegalMoveException(RULE_KING_IN_CHECK_AFTER_MOVE.getMessage());
        }
    }

    public boolean isLegalMove(Position from, Position to, Board board, Color currentTurn) {
        Piece piece = board.getPiece(from);

        if (piece == null) {
            return false;
        }

        if (piece.getColor() != currentTurn) {
            return false;
        }

        if (from.equals(to)) {
            return false;
        }

        Piece target = board.getPiece(to);
        if (target != null && target.getColor() == piece.getColor()) {
            return false;
        }

        if (!piece.isMoveValid(from, to, board)) {
            return false;
        }

        if (piece.getType() != Type.KNIGHT && board.hasObstacleInPath(from, to)) {
            return false;
        }

        if (isKingInCheckAfterMove(from, to, board, currentTurn)) {
            return false;
        }
        return true;
    }

    private boolean isKingInCheckAfterMove(Position from, Position to, Board board, Color kingColor) {
        Board virtualBoard = board.movePieceVirtually(from, to);

        return checkDetector.isCheck(virtualBoard, kingColor);
    }

    public boolean anyPieceHasLegalMove(Board board, Color currentColor) {
        return board.getPiecesByTeam(currentColor).entrySet().stream()
                .anyMatch(entry -> {
                    Position from = entry.getKey();

                    return pieceHasLegalMove(from, board, currentColor);
                });
    }

    private boolean pieceHasLegalMove(Position from, Board board, Color currentColor) {
        return IntStream.rangeClosed(0, 7)
                .anyMatch(x -> IntStream.rangeClosed(0, 7)
                        .anyMatch(y -> {
                            Position to = Position.of(x, y);

                            return isLegalMove(from, to, board, currentColor);
                        })
                );
    }
}