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

        Color fromPieceColor = fromPiece.getColor();
        Piece toPiece = board.getPiece(to);

        int dx = to.x() - from.x();
        int dy = to.y() - from.y();

        if (toPiece != null && toPiece.getColor().opposite() == fromPieceColor &&
                isAttackMove(dx, dy, fromPieceColor)) {
            return true;
        }

        if (toPiece == null && isNotAttackMove(dx, dy, from, fromPieceColor)) {
            return true;
        }

        return false;
    }

    private boolean isAttackMove(int dx, int dy, Color color) {
        int direction = color.getDirection();
        return Math.abs(dx) == 1 && dy == direction;
    }

    private boolean isNotAttackMove(int dx, int dy, Position from, Color color) {
        if (isOneStepMove(dx, dy, color)) {
            return true;
        }

        if (isTwoStepMove(dx, dy, from, color)) {
            return true;
        }

        return false;
    }

    private boolean isOneStepMove(int dx, int dy, Color color) {
        int direction = color.getDirection();
        return dx == 0 && dy == direction;
    }

    private boolean isTwoStepMove(int dx, int dy, Position from, Color color) {
        int startRank = color.getPawnStartRank();
        int direction = color.getDirection();

        return dx == 0 && dy == 2 * direction && from.y() == startRank;
    }
}