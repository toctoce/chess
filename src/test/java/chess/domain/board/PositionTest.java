package chess.domain.board;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import chess.common.exception.InvalidPositionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class PositionTest {

    @Nested
    @DisplayName("대수 기보 표기법으로 Position 생성 테스트")
    class FromMethodTest {
        @ParameterizedTest
        @CsvSource(value = {"A1,0,0", "B2,1,1", "C3,2,2", "D4,3,3", "E5,4,4", "F6,5,5", "G7,6,6", "H8,7,7"})
        @DisplayName("정상적인 대수 기보 입력 시, 0-7 좌표로 정확히 변환되어 객체가 생성된다")
        void validInput(String algebraicNotation, int expectedX, int expectedY) {
            Position position = Position.from(algebraicNotation);

            assertThat(position.x()).isEqualTo(expectedX);
            assertThat(position.x()).isEqualTo(expectedY);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"A11", "__", "a1", "I1", "11", "A0", "A9", "A 1"})
        @DisplayName("비정상적인 길이, 형식, 또는 범위 입력 시 InvalidPositionException이 발생한다")
        void invalidInput(String algebraicNotation) {
            assertThatThrownBy(() -> Position.from(algebraicNotation))
                    .isInstanceOf(InvalidPositionException.class);
        }
    }
}