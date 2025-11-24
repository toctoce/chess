package chess.domain.status;

import chess.domain.board.Board;
import chess.domain.board.Position;
import chess.domain.piece.Color;
import chess.domain.piece.Piece;
import chess.domain.piece.Type;

public class CheckDetector {

    public boolean isCheck(Board board, Color kingColor) {
        Position kingPosition = board.findKingPosition(kingColor);
        if (kingPosition == null) {
            return false;
        }
        return isSquareAttacked(board, kingPosition, kingColor.opposite());
    }

    public boolean isSquareAttacked(Board board, Position targetPosition, Color attackerColor) {
        return board.getPiecesByTeam(attackerColor).entrySet().stream()
                .anyMatch(entry -> isAttackValid(entry.getKey(), targetPosition, board, entry.getValue()));
    }

    private boolean isAttackValid(Position from, Position target, Board board, Piece attackerPiece) {
        if (!attackerPiece.isMoveValid(from, target, board)) {
            return false;
        }
        if (attackerPiece.getType() != Type.KNIGHT && board.hasObstacleInPath(from, target)) {
            return false;
        }
        return true;
    }
}