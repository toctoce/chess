package chess.domain.factory.impls;

import chess.domain.factory.PieceFactory;
import chess.domain.piece.Color;
import chess.domain.piece.Piece;
import chess.domain.piece.impls.Bishop;
import chess.domain.piece.impls.King;
import chess.domain.piece.impls.Knight;
import chess.domain.piece.impls.Pawn;
import chess.domain.piece.impls.Queen;
import chess.domain.piece.impls.Rook;

public class WhitePieceFactory implements PieceFactory {

    private static final Color COLOR = Color.WHITE;

    @Override
    public Piece createKing() {
        return new King(COLOR);
    }

    @Override
    public Piece createQueen() {
        return new Queen(COLOR);
    }

    @Override
    public Piece createRook() {
        return new Rook(COLOR);
    }

    @Override
    public Piece createBishop() {
        return new Bishop(COLOR);
    }

    @Override
    public Piece createKnight() {
        return new Knight(COLOR);
    }

    @Override
    public Piece createPawn() {
        return new Pawn(COLOR);
    }
}