package chess.domain.state.impls;

import static chess.common.message.ErrorMessage.GAME_ALREADY_FINISHED;

import chess.common.exception.GameFinishedException;
import chess.domain.board.Board;
import chess.domain.board.Position;
import chess.domain.piece.Color;
import chess.domain.state.GameState;
import chess.domain.state.GameStatus;

public class StalemateState implements GameState {

    @Override
    public GameState move(Position from, Position to, Board board, Color turnColor) {
        throw new GameFinishedException(GAME_ALREADY_FINISHED.getMessage(status().getDescription()));
    }

    @Override
    public boolean isFinished() {
        return true;
    }

    @Override
    public GameStatus status() {
        return GameStatus.STALEMATE_DRAW;
    }
}