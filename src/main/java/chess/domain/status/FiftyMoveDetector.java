package chess.domain.status;

import chess.domain.game.Game;
import chess.domain.game.GameHistory;

public class FiftyMoveDetector {

    private static final int LIMIT_COUNT = 100;

    public boolean isFiftyMove(Game game) {
        GameHistory history = game.getHistory();
        return history.getFiftyMoveCount() >= LIMIT_COUNT;
    }
}