package chess.domain.board;

import org.junit.jupiter.api.Assertions;
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
        void 정상_입력시_객체_생성(String algebraicNotation, int expectedX, int expectedY) {
            // when
            Position position = Position.from(algebraicNotation);

            // then
            Assertions.assertEquals(expectedX, position.x());
            Assertions.assertEquals(expectedY, position.y());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"A11", "__", "a1", "I1", "11", "A0", "A9", "A 1"})
        void 비정상_입력시_예외_발생(String algebraicNotation) {
            // when, then
            Assertions.assertThrows(IllegalArgumentException.class, () -> Position.from(algebraicNotation));
        }
    }
}