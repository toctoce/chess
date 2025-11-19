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

@DisplayName("Queen 구현체 테스트")
class QueenTest {

    private final Board emptyBoard = new Board();

    @Nested
    @DisplayName("Queen 생성자 및 속성")
    class ConstructorTest {

        @ParameterizedTest
        @EnumSource(Color.class)
        @DisplayName("유효한 Color로 생성 시 성공하며, 색상과 타입이 올바르다")
        void validCreationSuccess(Color color) {
            Queen queen = new Queen(color);

            assertThat(queen.getColor()).isEqualTo(color);
            assertThat(queen.getType().name()).isEqualTo("QUEEN");
        }

        @Test
        @DisplayName("null Color로 생성 시 PieceCreationException을 던진다")
        void nullColorThrowsException() {
            assertThatThrownBy(() -> new Queen(null))
                    .isInstanceOf(PieceCreationException.class)
                    .hasMessageContaining(PIECE_INVALID_CREATION_ARGUMENTS.getMessage());
        }
    }

    @ParameterizedTest
    @CsvSource(value = {"WHITE, Q", "BLACK, q"})
    @DisplayName("색상에 따라 정확한 심볼을 반환한다")
    void colorReturnsCorrectSymbol(Color color, String expectedSymbol) {
        Queen queen = new Queen(color);
        assertThat(queen.getSymbol()).isEqualTo(expectedSymbol);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "A1, A8", "B4, B7", "H5, H1", "C1, H1", "D5, A5", "E3, B3",
            "A1, H8", "D4, A7", "H1, A8"
    })
    @DisplayName("Queen의 규칙에 맞는 이동은 true를 반환한다")
    void validMoveReturnsTrue(String fromNotation, String toNotation) {
        Position from = Position.from(fromNotation);
        Position to = Position.from(toNotation);

        Queen queen = new Queen(Color.WHITE);

        assertThat(queen.isMoveValid(from, to, emptyBoard)).isTrue();
    }

    @ParameterizedTest
    @CsvSource(value = {"A1, A1", "A1, B3", "A1, B6", "A1, E3", "A1, E4", "D4, H6", "D4, H7"})
    @DisplayName("Queen의 규칙에 어긋나는 이동은 false를 반환한다")
    void invalidMoveReturnsFalse(String fromNotation, String toNotation) {
        Position from = Position.from(fromNotation);
        Position to = Position.from(toNotation);
        Queen queen = new Queen(Color.WHITE);

        assertThat(queen.isMoveValid(from, to, emptyBoard)).isFalse();
    }
}