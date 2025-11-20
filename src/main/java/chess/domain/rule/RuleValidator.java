package chess.domain.rule;

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
import java.util.HashMap;
import java.util.Map;

public class RuleValidator {

    private final CheckmateDetector checkmateDetector;

    public RuleValidator(CheckmateDetector checkmateDetector) {
        this.checkmateDetector = checkmateDetector;
    }

    // todo : 앙파상, 캐슬링 규칙 적용
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

        if (piece.getType() != Type.KNIGHT && board.hasObstacleInPath(from, to)) {
            throw new RuleViolationException(RULE_PATH_BLOCKED.getMessage());
        }

        if (isKingInCheckAfterMove(from, to, board, currentTurn)) {
            throw new IllegalMoveException(RULE_KING_IN_CHECK_AFTER_MOVE.getMessage());
        }
    }

    private boolean isKingInCheckAfterMove(Position from, Position to, Board board, Color kingColor) {
        Map<Position, Piece> copiedBoard = new HashMap<>(board.getPieces());
        Board virtualBoard = new Board(copiedBoard);

        virtualBoard.getPiece(from);
        virtualBoard.movePiece(from, to);

        return checkmateDetector.isCheckmate(virtualBoard, kingColor);
    }
}