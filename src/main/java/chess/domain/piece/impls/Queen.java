package chess.domain.piece.impls;

import chess.domain.piece.Color;
import chess.domain.piece.Piece;
import chess.domain.piece.Type;
import chess.domain.strategy.impls.QueenMovement;

public class Queen extends Piece {

    public Queen(Color color) {
        super(color, Type.QUEEN, new QueenMovement());
    }

    public Queen(Color color, boolean isMoved) {
        super(color, Type.QUEEN, new QueenMovement(), isMoved);
    }

    @Override
    public Piece afterMove() {
        return new Queen(this.getColor(), true);
    }

    @Override
    public String getSymbol() {
        if (getColor() == Color.WHITE) {
            return "Q";
        }
        return "q";
    }
}