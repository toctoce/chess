package chess.domain.piece.impls;

import chess.domain.piece.Color;
import chess.domain.piece.Piece;
import chess.domain.piece.Type;
import chess.domain.strategy.impls.RookMovement;

public class Rook extends Piece {

    public Rook(Color color) {
        super(color, Type.ROOK, new RookMovement());
    }

    private Rook(Color color, boolean isMoved) {
        super(color, Type.ROOK, new RookMovement(), isMoved);
    }

    @Override
    public Piece afterMove() {
        return new Rook(this.getColor(), true);
    }

    @Override
    public String getSymbol() {
        if (getColor() == Color.WHITE) {
            return "R";
        }
        return "r";
    }
}