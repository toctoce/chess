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

@DisplayName("King 구현체 테스트")
class KingTest {

    private final Board emptyBoard = new Board();

    @Nested
    @DisplayName("King 생성자 및 속성")
    class ConstructorTest {

        @ParameterizedTest
        @EnumSource(names = {"WHITE", "BLACK"})
        @DisplayName("유효한 Color로 생성 시 성공하며, 색상과 타입이 올바르다")
        void validCreationSuccess(Color color) {
            King king = new King(color);

            assertThat(king.getColor()).isEqualTo(color);
            assertThat(king.getType().name()).isEqualTo("KING");
        }

        @Test
        @DisplayName("null Color로 생성 시 PieceCreationException을 던진다")
        void nullColorThrowsException() {
            assertThatThrownBy(() -> new King(null))
                    .isInstanceOf(PieceCreationException.class)
                    .hasMessageContaining(PIECE_INVALID_CREATION_ARGUMENTS.getMessage());
        }
    }

    @ParameterizedTest
    @CsvSource(value = {"WHITE, K", "BLACK, k"})
    @DisplayName("색상에 따라 정확한 심볼을 반환한다")
    void colorReturnsCorrectSymbol(Color color, String expectedSymbol) {
        King king = new King(color);
        assertThat(king.getSymbol()).isEqualTo(expectedSymbol);
    }

    @ParameterizedTest
    @CsvSource(value = {"D4, C3", "D4, C4", "D4, C5", "D4, D3", "D4, D5", "D4, E3", "D4, E4", "D4, E5"})
    @DisplayName("King의 규칙에 맞는 이동은 true를 반환한다")
    void validMoveReturnsTrue(String fromNotation, String toNotation) {
        Position from = Position.from(fromNotation);
        Position to = Position.from(toNotation);

        King king = new King(Color.WHITE);

        assertThat(king.isMoveValid(from, to, emptyBoard)).isTrue();
    }

    @ParameterizedTest
    @CsvSource(value = {"A1, A1", "D4, D6", "D4, A4", "B2, D4"})
    @DisplayName("King의 규칙에 어긋나는 이동은 false를 반환한다")
    void invalidMoveReturnsFalse(String fromNotation, String toNotation) {
        Position from = Position.from(fromNotation);
        Position to = Position.from(toNotation);
        King king = new King(Color.WHITE);

        assertThat(king.isMoveValid(from, to, emptyBoard)).isFalse();
    }
}
