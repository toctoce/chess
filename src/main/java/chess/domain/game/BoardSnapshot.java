package chess.domain.game;

import chess.domain.board.Position;
import chess.domain.piece.Color;
import chess.domain.piece.Piece;
import java.util.Map;

public record BoardSnapshot(Map<Position, Piece> pieces, Color turn) {

    public BoardSnapshot(Map<Position, Piece> pieces, Color turn) {
        this.pieces = Map.copyOf(pieces);
        this.turn = turn;
    }
}