package chess.domain.piece.impls;

import chess.domain.piece.Color;
import chess.domain.piece.Piece;
import chess.domain.piece.Type;
import chess.domain.strategy.impls.BishopMovement;

public class Bishop extends Piece {
    public Bishop(Color color) {
        super(color, Type.BISHOP, new BishopMovement());
    }

    @Override
    public String getSymbol() {
        if (getColor() == Color.WHITE) {
            return "B";
        }
        return "b";
    }
}