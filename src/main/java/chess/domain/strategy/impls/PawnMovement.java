package chess.domain.strategy.impls;

import static chess.common.message.ErrorMessage.PIECE_NOT_FOUND;

import chess.common.exception.PieceNotFoundException;
import chess.domain.board.Board;
import chess.domain.board.Position;
import chess.domain.piece.Color;
import chess.domain.piece.Piece;
import chess.domain.strategy.MovementStrategy;

public class PawnMovement implements MovementStrategy {

    @Override
    public boolean isMoveValid(Position from, Position to, Board board) {
        Piece fromPiece = board.getPiece(from);
        if (fromPiece == null) {
            throw new PieceNotFoundException(PIECE_NOT_FOUND.getMessage());
        }

        Color selfColor = fromPiece.getColor();
        Piece target = board.getPiece(to);

        int dx = to.x() - from.x();
        int dy = to.y() - from.y();

        if (isAttackMove(dx, dy, selfColor, target)) {
            return true;
        }

        if (isNotAttackMove(dx, dy, from, selfColor, target)) {
            return true;
        }

        return false;
    }

    private boolean isAttackMove(int dx, int dy, Color selfColor, Piece target) {
        if (target == null || target.getColor() == selfColor) {
            return false;
        }

        int direction = getPawnDirection(selfColor);
        return Math.abs(dx) == 1 && dy == direction;
    }

    private boolean isNotAttackMove(int dx, int dy, Position from, Color selfColor, Piece target) {
        if (target != null) {
            return false;
        }

        if (isOneStepMove(dx, dy, selfColor)) {
            return true;
        }

        if (isTwoStepMove(dx, dy, from, selfColor)) {
            // TODO: RuleValidator에서 중간 경로 비어있는지 검사 필요 (여기서는 규칙 패턴만 검사)
            return true;
        }

        return false;
    }

    private boolean isOneStepMove(int dx, int dy, Color selfColor) {
        int direction = getPawnDirection(selfColor);
        return dx == 0 && dy == direction;
    }

    private boolean isTwoStepMove(int dx, int dy, Position from, Color selfColor) {
        int startRank = getStartRank(selfColor);
        int direction = getPawnDirection(selfColor);

        return dx == 0 && dy == 2 * direction && from.y() == startRank;
    }

    private static int getStartRank(Color color) {
        if (color == Color.WHITE) {
            return 1;
        }
        return 6;
    }

    private int getPawnDirection(Color color) {
        if (color == Color.WHITE) {
            return 1;
        }
        return -1;
    }
}