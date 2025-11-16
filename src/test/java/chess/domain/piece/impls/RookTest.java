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

@DisplayName("Rook 구현체 테스트")
class RookTest {

    private final Board emptyBoard = new Board();
    private final Position a1 = Position.from("A1");
    private final Position a2 = Position.from("A2");
    private final Position b2 = Position.from("B2");

    @Nested
    @DisplayName("Rook 생성자 및 속성")
    class ConstructorTest {

        @ParameterizedTest
        @EnumSource(Color.class)
        @DisplayName("유효한 Color로 생성 시 성공하며, 색상과 타입이 올바르다")
        void valid_creation_success(Color color) {
            Rook rook = new Rook(color);

            assertThat(rook.getColor()).isEqualTo(color);
            assertThat(rook.getType().name()).isEqualTo("ROOK");
        }

        @Test
        @DisplayName("null Color로 생성 시 InvalidPieceCreationException을 던진다")
        void null_color_throws_exception() {
            // Piece 생성자에서 null 체크 로직이 정상 작동하는지 검증
            assertThatThrownBy(() -> new Rook(null))
                    .isInstanceOf(PieceCreationException.class)
                    .hasMessageContaining(PIECE_INVALID_CREATION_ARGUMENTS.getMessage());
        }
    }

    @ParameterizedTest
    @CsvSource(value = {"WHITE, R", "BLACK, r"})
    @DisplayName("색상에 따라 정확한 심볼을 반환한다")
    void color_returns_correct_symbol(Color color, String expectedSymbol) {
        Rook rook = new Rook(color);

        assertThat(rook.getSymbol()).isEqualTo(expectedSymbol);
    }

    @ParameterizedTest
    @CsvSource(value = {"A1, A8", "B4, B7", "H5, H1", "C1, H1", "D5, A5", "E3, B3"})
    @DisplayName("Rook의 규칙에 맞는 이동은 true를 반환한다")
    void valid_straight_move_returns_true(String fromNotation, String toNotation) {
        Position from = Position.from(fromNotation);
        Position to = Position.from(toNotation);

        Rook rook = new Rook(Color.WHITE);

        assertThat(rook.isMoveValid(from, to, emptyBoard)).isTrue();
    }

    @ParameterizedTest
    @CsvSource(value = {"A1, B2", "E4, G6", "H8, A1", "A1, B3", "C1, F2"})
    @DisplayName("Rook의 규칙에 어긋나는 이동은 false를 반환한다")
    void invalid_non_straight_move_returns_false(String fromNotation, String toNotation) {
        Position from = Position.from(fromNotation);
        Position to = Position.from(toNotation);
        Rook rook = new Rook(Color.WHITE);

        assertThat(rook.isMoveValid(from, to, emptyBoard)).isFalse();
    }
}