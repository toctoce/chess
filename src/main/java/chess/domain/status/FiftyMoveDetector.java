package chess.domain.status;

import chess.domain.game.GameHistory;

public class FiftyMoveDetector {

    private static final int LIMIT_COUNT = 100;

    public boolean isFiftyMove(GameHistory history) {
        return history.getFiftyMoveCount() >= LIMIT_COUNT;
    }
}