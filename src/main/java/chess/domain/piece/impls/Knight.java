package chess.domain.piece.impls;

import chess.domain.piece.Color;
import chess.domain.piece.Piece;
import chess.domain.piece.Type;
import chess.domain.strategy.impls.KnightMovement;

public class Knight extends Piece {
    
    public Knight(Color color) {
        super(color, Type.KNIGHT, new KnightMovement());
    }

    private Knight(Color color, boolean isMoved) {
        super(color, Type.KNIGHT, new KnightMovement(), isMoved);
    }

    @Override
    public Piece afterMove() {
        return new Knight(this.getColor(), true);
    }

    @Override
    public String getSymbol() {
        if (getColor() == Color.WHITE) {
            return "N";
        }
        return "n";
    }
}