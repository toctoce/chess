package chess.domain.rule;

import chess.domain.board.Board;
import chess.domain.board.Position;
import chess.domain.piece.Color;
import chess.domain.piece.Piece;
import chess.domain.piece.Type;
import java.util.Map;

public class CheckDetector {

    public boolean isCheckmate(Board board, Color kingColor) {
        Position kingPosition = findKingPosition(board, kingColor);

        if (kingPosition == null) {
            return false;
        }

        Color opposite = kingColor.opposite();

        return board.getPieces().entrySet().stream()
                .filter(entry -> entry.getValue().getColor() == opposite) // 상대 기물만 필터링
                .anyMatch(entry -> {
                    Position attackerPosition = entry.getKey();
                    Piece attackerPiece = entry.getValue();

                    return isAttackValid(attackerPosition, kingPosition, board, attackerPiece);
                });
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

    private Position findKingPosition(Board board, Color kingColor) {
        return board.getPieces().entrySet().stream()
                .filter(entry -> entry.getValue().getType() == Type.KING && entry.getValue().getColor() == kingColor)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }
}