package chess.domain.piece.impls;

import chess.domain.piece.Color;
import chess.domain.piece.Piece;
import chess.domain.piece.Type;
import chess.domain.strategy.impls.PawnMovement;

public class Pawn extends Piece {

    public Pawn(Color color) {
        super(color, Type.PAWN, new PawnMovement());
    }

    private Pawn(Color color, boolean isMoved) {
        super(color, Type.PAWN, new PawnMovement(), isMoved);
    }

    @Override
    public Piece afterMove() {
        return new Pawn(this.getColor(), true);
    }

    @Override
    public String getSymbol() {
        if (getColor() == Color.WHITE) {
            return "P";
        }
        return "p";
    }
}