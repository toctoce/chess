package chess.domain.strategy.impls;

import chess.domain.board.Board;
import chess.domain.board.Position;
import chess.domain.strategy.MovementStrategy;

public class RookMovement implements MovementStrategy {

    @Override
    public boolean isMoveValid(Position from, Position to, Board board) {
        int dx = Math.abs(from.x() - to.x());
        int dy = Math.abs(from.y() - to.y());

        return (dx == 0 && dy != 0) || (dx != 0 && dy == 0);
    }
}