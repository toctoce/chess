package chess.domain.game;

import static chess.common.message.ErrorMessage.NO_HISTORY;

import chess.common.exception.EmptyHistoryException;
import chess.domain.board.Board;
import chess.domain.piece.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class GameHistory {

    private int fiftyMoveCount;
    private final Map<BoardSnapshot, Integer> repetitionCounter;

    // undo용 스택
    private final Stack<BoardSnapshot> boardStack;
    private final Stack<Integer> fiftyMoveStack;

    public GameHistory() {
        this.fiftyMoveCount = 0;
        this.repetitionCounter = new HashMap<>();

        this.boardStack = new Stack<>();
        this.fiftyMoveStack = new Stack<>();
    }

    public void updateHistory(Board board, Color turnColor, boolean resetFiftyMoveCount) {
        updateFiftyMoveCount(resetFiftyMoveCount);
        updateRepetitionCounter(board, turnColor);
    }

    public void saveHistory(Board board, Color turnColor) {
        BoardSnapshot snapshot = new BoardSnapshot(board.getPieces(), turnColor);
        boardStack.push(snapshot);
        fiftyMoveStack.push(fiftyMoveCount);
    }

    public BoardSnapshot undoHistory(Board board, Color turnColor) {
        if (boardStack.isEmpty()) {
            throw new EmptyHistoryException(NO_HISTORY.getMessage());
        }
        this.fiftyMoveCount = fiftyMoveStack.pop();

        BoardSnapshot snapshot = new BoardSnapshot(board.getPieces(), turnColor);
        decreaseRepetitionCount(snapshot);
        return boardStack.pop();
    }

    private void decreaseRepetitionCount(BoardSnapshot snapshot) {
        if (!repetitionCounter.containsKey(snapshot)) {
            return;
        }
        int count = repetitionCounter.get(snapshot);
        if (count <= 1) {
            repetitionCounter.remove(snapshot);
            return;
        }
        repetitionCounter.put(snapshot, count - 1);
    }

    private void updateFiftyMoveCount(boolean reset) {
        if (reset) {
            this.fiftyMoveCount = 0;
            return;
        }
        this.fiftyMoveCount++;
    }

    private void updateRepetitionCounter(Board board, Color nextTurnColor) {
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