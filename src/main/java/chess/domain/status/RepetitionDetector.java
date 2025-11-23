package chess.domain.status;

import chess.domain.board.Board;
import chess.domain.game.Game;
import chess.domain.game.GameHistory;
import chess.domain.piece.Color;

public class RepetitionDetector {

    private static final int REPETITION_LIMIT = 3;

    public boolean isRepetition(Game game) {
        GameHistory history = game.getHistory();
        Board board = game.getBoard();
        Color currentTurn = game.getCurrentTurn();

        return history.getRepetitionCount(board, currentTurn) >= REPETITION_LIMIT;
    }
}