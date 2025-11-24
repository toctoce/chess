package chess.domain.game;

import chess.domain.board.Position;
import chess.domain.piece.Color;
import chess.domain.piece.Piece;
import java.util.Map;

public record BoardSnapshot(Map<Position, Piece> pieces, Color turn, Position enPassantTarget) {

    public BoardSnapshot(Map<Position, Piece> pieces, Color turn, Position enPassantTarget) {
        this.pieces = Map.copyOf(pieces);
        this.turn = turn;
        this.enPassantTarget = enPassantTarget;
    }
}