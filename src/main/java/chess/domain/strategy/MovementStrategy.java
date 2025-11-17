package chess.domain.strategy;

import chess.domain.board.Board;
import chess.domain.board.Position;

public interface MovementStrategy {
    boolean isMoveValid(Position from, Position to, Board board);
}