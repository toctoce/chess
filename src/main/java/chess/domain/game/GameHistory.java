package chess.domain.game;

import chess.domain.board.Board;
import chess.domain.piece.Color;
import java.util.HashMap;
import java.util.Map;

public class GameHistory {

    private int fiftyMoveCount;
    private final Map<BoardSnapshot, Integer> repetitionCounter;

    public GameHistory() {
        this.fiftyMoveCount = 0;
        this.repetitionCounter = new HashMap<>();
    }

    public void updateFiftyMoveCount(boolean reset) {
        if (reset) {
            this.fiftyMoveCount = 0;
            return;
        }
        this.fiftyMoveCount++;
    }

    public void updateRepetitionCounter(Board board, Color nextTurnColor) {
        BoardSnapshot snapshot = new BoardSnapshot(board.getPieces(), nextTurnColor);
        int count = repetitionCounter.getOrDefault(snapshot, 0) + 1;
        repetitionCounter.put(snapshot, count);
    }

    public int getRepetitionCount(Board board, Color currentTurn) {
        BoardSnapshot snapshot = new BoardSnapshot(board.getPieces(), currentTurn);
        return repetitionCounter.getOrDefault(snapshot, 0);
    }

    public int getFiftyMoveCount() {
        return fiftyMoveCount;
    }

    public Map<BoardSnapshot, Integer> getRepetitionCounter() {
        return repetitionCounter;
    }
}