package chess.domain.state.impls;

import static chess.common.message.ErrorMessage.GAME_ALREADY_FINISHED;

import chess.common.exception.GameFinishedException;
import chess.domain.board.Board;
import chess.domain.board.Position;
import chess.domain.piece.Color;
import chess.domain.state.GameState;
import chess.domain.state.GameStatus;

public class CheckmateState implements GameState {

    private final Color winnerColor;

    public CheckmateState(Color winnerColor) {
        this.winnerColor = winnerColor;
    }

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
        if (winnerColor == Color.WHITE) {
            return GameStatus.CHECKMATE_WHITE_WIN;
        }
        return GameStatus.CHECKMATE_BLACK_WIN;
    }
}