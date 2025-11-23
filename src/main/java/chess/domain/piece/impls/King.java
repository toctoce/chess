package chess.domain.piece.impls;

import chess.domain.piece.Color;
import chess.domain.piece.Piece;
import chess.domain.piece.Type;
import chess.domain.strategy.impls.KingMovement;

public class King extends Piece {

    public King(Color color) {
        super(color, Type.KING, new KingMovement());
    }

    private King(Color color, boolean isMoved) {
        super(color, Type.KING, new KingMovement(), isMoved);
    }

    @Override
    public Piece afterMove() {
        return new King(this.getColor(), true);
    }

    @Override
    public String getSymbol() {
        if (getColor() == Color.WHITE) {
            return "K";
        }
        return "k";
    }
}