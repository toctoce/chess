package chess.domain.piece.impls;

import static chess.common.message.ErrorMessage.PIECE_INVALID_CREATION_ARGUMENTS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import chess.common.exception.PieceCreationException;
import chess.domain.board.Board;
import chess.domain.board.Position;
import chess.domain.piece.Color;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

@DisplayName("Knight 구현체 테스트")
class KnightTest {

    private final Board emptyBoard = new Board();

    @Nested
    @DisplayName("Knight 생성자 및 속성")
    class ConstructorTest {

        @ParameterizedTest
        @EnumSource(names = {"WHITE", "BLACK"})
        @DisplayName("유효한 Color로 생성 시 성공하며, 색상과 타입이 올바르다")
        void valid_creation_success(Color color) {
            Knight knight = new Knight(color);

            assertThat(knight.getColor()).isEqualTo(color);
            assertThat(knight.getType().name()).isEqualTo("KNIGHT");
        }

        @Test
        @DisplayName("null Color로 생성 시 PieceCreationException을 던진다")
        void null_color_throws_exception() {
            assertThatThrownBy(() -> new Knight(null))
                    .isInstanceOf(PieceCreationException.class)
                    .hasMessageContaining(PIECE_INVALID_CREATION_ARGUMENTS.getMessage());
        }
    }

    @ParameterizedTest
    @CsvSource(value = {"WHITE, N", "BLACK, n"})
    @DisplayName("색상에 따라 정확한 심볼을 반환한다")
    void color_returns_correct_symbol(Color color, String expectedSymbol) {
        Knight knight = new Knight(color);
        assertThat(knight.getSymbol()).isEqualTo(expectedSymbol);
    }

    @ParameterizedTest
    @CsvSource(value = {"D4, C2", "D4, B3", "D4, B5", "D4, C6", "D4, E6", "D4, F5", "D4, F3", "D4, E2"})
    @DisplayName("Knight의 규칙에 맞는 이동은 true를 반환한다")
    void valid_move_returns_true(String fromNotation, String toNotation) {
        Position from = Position.from(fromNotation);
        Position to = Position.from(toNotation);

        Knight knight = new Knight(Color.WHITE);

        assertThat(knight.isMoveValid(from, to, emptyBoard)).isTrue();
    }

    @ParameterizedTest
    @CsvSource(value = {"A1, A1", "D4, D7", "D4, F4", "B2, D4"})
    @DisplayName("Knight의 규칙에 어긋나는 이동은 false를 반환한다")
    void invalid_move_returns_false(String fromNotation, String toNotation) {
        Position from = Position.from(fromNotation);
        Position to = Position.from(toNotation);
        Knight knight = new Knight(Color.WHITE);

        assertThat(knight.isMoveValid(from, to, emptyBoard)).isFalse();
    }
}
