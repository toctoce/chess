package chess.domain.status;

import chess.domain.board.Board;
import chess.domain.game.Game;
import chess.domain.piece.Color;
import chess.domain.piece.Piece;
import chess.domain.piece.Type;
import java.util.List;

public class InsufficientMaterialDetector {

    public boolean isInsufficientMaterial(Game game) {
        Board board = game.getBoard();
        List<Piece> whitePieces = board.getPiecesByTeam(Color.WHITE).values().stream().toList();
        List<Piece> blackPieces = board.getPiecesByTeam(Color.BLACK).values().stream().toList();

        if (kingVsKing(whitePieces, blackPieces) ||
                kingVsKingAndMinor(whitePieces, blackPieces) ||
                kingAndMinorVsKingAndMinor(whitePieces, blackPieces) ||
                kingVsKingAndTwoKnights(whitePieces, blackPieces)) {
            return true;
        }

        return false;
    }

    private boolean kingVsKingAndTwoKnights(List<Piece> whitePieces, List<Piece> blackPieces) {
        return (hasOnlyKing(whitePieces) && hasKingAndTwoKnights(blackPieces)) ||
                (hasOnlyKing(blackPieces) && hasKingAndTwoKnights(whitePieces));
    }

    private boolean kingAndMinorVsKingAndMinor(List<Piece> whitePieces, List<Piece> blackPieces) {
        return hasKingAndOneMinorPiece(whitePieces) && hasKingAndOneMinorPiece(blackPieces);
    }

    private boolean kingVsKingAndMinor(List<Piece> whitePieces, List<Piece> blackPieces) {
        return (hasOnlyKing(whitePieces) && hasKingAndOneMinorPiece(blackPieces)) ||
                (hasOnlyKing(blackPieces) && hasKingAndOneMinorPiece(whitePieces));
    }

    private boolean kingVsKing(List<Piece> whitePieces, List<Piece> blackPieces) {
        return hasOnlyKing(whitePieces) && hasOnlyKing(blackPieces);
    }

    private boolean hasOnlyKing(List<Piece> pieces) {
        return pieces.size() == 1 && hasKing(pieces);
    }

    private boolean hasKingAndOneMinorPiece(List<Piece> pieces) {
        return pieces.size() == 2 && hasKing(pieces) && hasMinorPiece(pieces);
    }

    private boolean hasKingAndTwoKnights(List<Piece> pieces) {
        return pieces.size() == 3 && hasKing(pieces) && countKnights(pieces) == 2;
    }

    private boolean hasKing(List<Piece> pieces) {
        return pieces.stream()
                .anyMatch(p -> p.getType() == Type.KING);
    }

    private boolean hasMinorPiece(List<Piece> pieces) {
        return pieces.stream()
                .anyMatch(Piece::isMinor);
    }

    private int countKnights(List<Piece> pieces) {
        return (int) pieces.stream()
                .filter(p -> p.getType() == Type.KNIGHT)
                .count();
    }
}