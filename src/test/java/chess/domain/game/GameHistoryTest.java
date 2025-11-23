package chess.domain.game;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import chess.domain.board.Board;
import chess.domain.piece.Color;
import java.util.HashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class GameHistoryTest {

    private GameHistory history;
    private Board board;

    @BeforeEach
    void setUp() {
        history = new GameHistory();
        board = new Board(new HashMap<>());
    }

    @Test
    @DisplayName("이동 전 상태를 저장하고 이동 후 상태로 업데이트하면 기록이 남는다")
    void saveAndUpdateHistory() {
        history.saveHistory(board, Color.WHITE);
        history.updateHistory(board, Color.BLACK, false);

        assertThat(history.getFiftyMoveCount()).isEqualTo(1);
        assertThat(history.getRepetitionCount(board, Color.BLACK)).isEqualTo(1);
    }

    @Test
    @DisplayName("Undo를 수행하면 이전 50수 카운트가 복구되고 현재 상태의 반복 카운트가 감소한다")
    void undoHistoryRestoresState() {
        history.saveHistory(board, Color.WHITE);
        history.updateHistory(board, Color.BLACK, false);
        BoardSnapshot restoredSnapshot = history.undoHistory(board, Color.BLACK);

        assertThat(restoredSnapshot.turn()).isEqualTo(Color.WHITE);
        assertThat(history.getFiftyMoveCount()).isEqualTo(0);
        assertThat(history.getRepetitionCount(board, Color.BLACK)).isEqualTo(0);
    }

    @Test
    @DisplayName("저장된 기록이 없는데 Undo를 시도하면 예외가 발생한다")
    void undoEmptyHistoryThrowsException() {
        assertThatThrownBy(() -> history.undoHistory(board, Color.WHITE));
    }

    @Nested
    @DisplayName("updateHistory 테스트")
    class updateHistoryTest {

        @Test
        @DisplayName("50수 리셋이 false면 카운트가 증가하고, 반복 횟수가 기록된다")
        void updateHistoryIncrementsCounts() {
            history.updateHistory(board, Color.BLACK, false);

            assertThat(history.getFiftyMoveCount()).isEqualTo(1);
            assertThat(history.getRepetitionCount(board, Color.BLACK)).isEqualTo(1);
        }

        @Test
        @DisplayName("50수 리셋이 true면 카운트가 0이 되고, 반복 횟수가 기록된다")
        void updateHistoryResetsFiftyMoveCount() {
            history.updateHistory(board, Color.BLACK, false);
            history.updateHistory(board, Color.WHITE, true);

            assertThat(history.getFiftyMoveCount()).isEqualTo(0);
            assertThat(history.getRepetitionCount(board, Color.WHITE)).isEqualTo(1);
        }

        @Test
        @DisplayName("동일한 상태가 반복되면 반복 카운트가 누적된다")
        void updateHistoryAccumulatesRepetitionCount() {
            history.updateHistory(board, Color.WHITE, false);
            history.updateHistory(board, Color.WHITE, false);
            history.updateHistory(board, Color.WHITE, false);

            assertThat(history.getRepetitionCount(board, Color.WHITE)).isEqualTo(3);
        }
    }
}