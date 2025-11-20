package chess.domain.state.impls;

import static chess.common.message.ErrorMessage.GAME_ALREADY_FINISHED;

import chess.common.exception.GameFinishedException;
import chess.domain.board.Board;
import chess.domain.board.Position;
import chess.domain.piece.Color;
import chess.domain.state.GameState;

public class CheckmateState implements GameState {

    private final Color winnerColor;

    public CheckmateState(Color winnerColor) {
        this.winnerColor = winnerColor;
    }

    @Override
    public GameState move(Position from, Position to, Board board, Color turnColor) {
        throw new GameFinishedException(
                GAME_ALREADY_FINISHED.getMessage(String.format("%s승(체크메이트)", turnColor.name()))
        );
    }

    @Override
    public boolean isFinished() {
        return true;
    }

    @Override
    public String status() {
        return "CHECKMATE_WINNER_" + winnerColor.name();
    }
}