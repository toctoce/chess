package chess.domain.piece.impls;

import chess.domain.piece.Color;
import chess.domain.piece.Piece;
import chess.domain.piece.Type;
import chess.domain.strategy.impls.QueenMovement;

public class Queen extends Piece {
    public Queen(Color color) {
        super(color, Type.QUEEN, new QueenMovement());
    }

    @Override
    public String getSymbol() {
        if (getColor() == Color.WHITE) {
            return "Q";
        }
        return "q";
    }
}