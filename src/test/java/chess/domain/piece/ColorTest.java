package chess.domain.piece;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@DisplayName("Color Enum 테스트")
class ColorTest {

    @Test
    @DisplayName("Color Enum은 WHITE, BLACK, NONE 세 가지 상수를 가진다")
    void colorEnumHasThreeConstants() {
        Color[] colors = Color.values();
        assertThat(colors).hasSize(2);
        assertThat(colors).containsExactlyInAnyOrder(Color.WHITE, Color.BLACK);
    }

    @ParameterizedTest
    @EnumSource(Color.class)
    @DisplayName("opposite() 메서드는 반대 색상을 정확히 반환한다")
    void oppositeReturnsCorrectColor(Color color) {
        if (color == Color.WHITE) {
            assertThat(color.opposite()).isEqualTo(Color.BLACK);
        }
        if (color == Color.BLACK) {
            assertThat(color.opposite()).isEqualTo(Color.WHITE);
        }
    }
}