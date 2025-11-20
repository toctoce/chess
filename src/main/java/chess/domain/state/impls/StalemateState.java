package chess.domain.state.impls;

import chess.common.exception.GameFinishedException;
import chess.common.message.ErrorMessage;
import chess.domain.board.Board;
import chess.domain.board.Position;
import chess.domain.piece.Color;
import chess.domain.state.GameState;

public class StalemateState implements GameState {

    @Override
    public GameState move(Position from, Position to, Board board, Color turnColor) {
        throw new GameFinishedException(ErrorMessage.GAME_ALREADY_FINISHED.getMessage("스테일메이트"));
    }

    @Override
    public boolean isFinished() {
        return true;
    }

    @Override
    public String status() {
        return "STALEMATE_DRAW";
    }
}