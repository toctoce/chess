package chess.domain.status;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import chess.domain.board.Board;
import chess.domain.game.Game;
import chess.domain.game.GameHistory;
import chess.domain.piece.Color;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("3회 반복 규칙 테스트")
class RepetitionDetectorTest {

    @Mock
    private Game game;

    @Mock
    private GameHistory gameHistory;

    @Mock
    private Board board;

    @InjectMocks
    private RepetitionDetector repetitionDetector;

    @ParameterizedTest(name = "반복 횟수가 {0}회일 때 3회 반복 조건 충족 여부는 {1}이다")
    @CsvSource(value = {
            "2, false",
            "3, true",
            "4, true"
    })
    void isRepetitionReturnsCorrectResult(int repetitionCount, boolean expectedResult) {
        Color currentTurn = Color.WHITE;

        when(game.getHistory()).thenReturn(gameHistory);
        when(game.getBoard()).thenReturn(board);
        when(game.getCurrentTurn()).thenReturn(currentTurn);

        when(gameHistory.getRepetitionCount(board, currentTurn)).thenReturn(repetitionCount);

        boolean actualResult = repetitionDetector.isRepetition(game);

        assertThat(actualResult).isEqualTo(expectedResult);
    }
}