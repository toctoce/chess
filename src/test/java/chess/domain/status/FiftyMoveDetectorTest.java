package chess.domain.status;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import chess.domain.game.Game;
import chess.domain.game.GameHistory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("50수 규칙 테스트")
class FiftyMoveDetectorTest {

    @Mock
    private Game game;

    @Mock
    private GameHistory gameHistory;

    @InjectMocks
    private FiftyMoveDetector fiftyMoveDetector;

    @ParameterizedTest(name = "50수 카운트가 {0}일 때 조건 충족 여부는 {1}이다")
    @CsvSource(value = {
            "99, false",
            "100, true",
            "101, true"
    })
    void isFiftyMoveReturnsCorrectResult(int moveCount, boolean expectedResult) {
        when(game.getHistory()).thenReturn(gameHistory);
        when(gameHistory.getFiftyMoveCount()).thenReturn(moveCount);

        boolean actualResult = fiftyMoveDetector.isFiftyMove(game);

        assertThat(actualResult).isEqualTo(expectedResult);
    }
}