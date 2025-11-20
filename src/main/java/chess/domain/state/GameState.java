package chess.domain.state;

import chess.domain.board.Board;
import chess.domain.board.Position;
import chess.domain.piece.Color;

public interface GameState {

    GameState move(Position from, Position to, Board board, Color turnColor);

    boolean isFinished();

    GameStatus status();
}