package chess.domain.piece.impls;

import static chess.common.message.ErrorMessage.PIECE_INVALID_CREATION_ARGUMENTS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import chess.common.exception.PieceCreationException;
import chess.domain.board.Board;
import chess.domain.board.Position;
import chess.domain.piece.Color;
import chess.domain.piece.Piece;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

@DisplayName("Pawn 구현체 테스트")
class PawnTest {

    @Nested
    @DisplayName("Pawn 생성자 및 속성")
    class ConstructorTest {

        @ParameterizedTest
        @EnumSource(Color.class)
        @DisplayName("유효한 Color로 생성 시 성공하며, 색상과 타입이 올바르다")
        void validCreationSuccess(Color color) {
            Pawn pawn = new Pawn(color);

            assertThat(pawn.getColor()).isEqualTo(color);
            assertThat(pawn.getType().name()).isEqualTo("PAWN");
        }

        @Test
        @DisplayName("null Color로 생성 시 PieceCreationException을 던진다")
        void nullColorThrowsException() {
            assertThatThrownBy(() -> new Pawn(null))
                    .isInstanceOf(PieceCreationException.class)
                    .hasMessageContaining(PIECE_INVALID_CREATION_ARGUMENTS.getMessage());
        }
    }

    @ParameterizedTest
    @CsvSource(value = {"WHITE, P", "BLACK, p"})
    @DisplayName("색상에 따라 정확한 심볼을 반환한다")
    void colorReturnsCorrectSymbol(Color color, String expectedSymbol) {
        Pawn pawn = new Pawn(color);
        assertThat(pawn.getSymbol()).isEqualTo(expectedSymbol);
    }

    @ParameterizedTest
    @CsvSource(value = {"A2, A3", "B4, B5", "A2, A4"})
    @DisplayName("White Pawn의 규칙에 맞는 이동은 true를 반환한다")
    void validWhiteMoveReturnsTrue(String fromNotation, String toNotation) {
        Position from = Position.from(fromNotation);
        Position to = Position.from(toNotation);
        Pawn pawn = new Pawn(Color.WHITE);

        Board board = getBoard(from, pawn);

        assertThat(pawn.isMoveValid(from, to, board)).isTrue();
    }


    @ParameterizedTest
    @CsvSource(value = {
            "A1, A1", "A2, A1", "A3, A5", "A2, B2", "A2, C4",
            "A2, B3", "A3, B4", "D5, E6", "D5, C6"
    })
    @DisplayName("White Pawn의 규칙에 어긋나는 이동은 false를 반환한다")
    void invalidWhiteMoveReturnsTrue(String fromNotation, String toNotation) {
        Position from = Position.from(fromNotation);
        Position to = Position.from(toNotation);
        Pawn pawn = new Pawn(Color.WHITE);

        Board board = getBoard(from, pawn);

        assertThat(pawn.isMoveValid(from, to, board)).isFalse();
    }

    @ParameterizedTest
    @CsvSource(value = {"A2, B3", "A3, B4", "D5, E6", "D5, C6"})
    @DisplayName("White Pawn의 공격 이동은 true를 반환한다")
    void invalidWhiteAttackMoveReturnsTrue(String fromNotation, String toNotation) {
        Position from = Position.from(fromNotation);
        Position to = Position.from(toNotation);
        Pawn fromPawn = new Pawn(Color.WHITE);
        Pawn toPawn = new Pawn(Color.BLACK);

        Board board = getTwoPiecesBoard(from, fromPawn, to, toPawn);

        assertThat(fromPawn.isMoveValid(from, to, board)).isTrue();
    }

    @ParameterizedTest
    @CsvSource(value = {"A7, A6", "B5, B4", "A7, A5"})
    @DisplayName("Black Pawn의 규칙에 맞는 이동은 true를 반환한다")
    void validBlackMoveReturnsTrue(String fromNotation, String toNotation) {
        Position from = Position.from(fromNotation);
        Position to = Position.from(toNotation);
        Pawn pawn = new Pawn(Color.BLACK);

        Board board = getBoard(from, pawn);

        assertThat(pawn.isMoveValid(from, to, board)).isTrue();
    }

    @ParameterizedTest
    @CsvSource(value = {
            "A7, A8", "B6, B4", "H7, G7", "D7, F5",
            "A7, B6", "A6, B5", "E5, D4", "E5, D4"
    })
    @DisplayName("Black Pawn의 규칙에 어긋나는 이동은 false를 반환한다")
    void invalidBlackMoveReturnsTrue(String fromNotation, String toNotation) {
        Position from = Position.from(fromNotation);
        Position to = Position.from(toNotation);
        Pawn pawn = new Pawn(Color.BLACK);

        Board board = getBoard(from, pawn);

        assertThat(pawn.isMoveValid(from, to, board)).isFalse();
    }

    @ParameterizedTest
    @CsvSource(value = {"A7, B6", "A6, B5", "E5, D4", "E5, D4"})
    @DisplayName("Black Pawn의 공격 이동은 true를 반환한다")
    void invalid_black_attack_move_returns_true(String fromNotation, String toNotation) {
        Position from = Position.from(fromNotation);
        Position to = Position.from(toNotation);
        Pawn fromPawn = new Pawn(Color.BLACK);
        Pawn toPawn = new Pawn(Color.WHITE);

        Board board = getTwoPiecesBoard(from, fromPawn, to, toPawn);

        assertThat(fromPawn.isMoveValid(from, to, board)).isTrue();
    }

    private static Board getBoard(Position position, Pawn pawn) {
        Map<Position, Piece> initialPieces = new HashMap<>();
        initialPieces.put(position, pawn);
        return new Board(initialPieces);
    }

    private static Board getTwoPiecesBoard(Position from, Pawn fromPawn, Position to, Pawn toPawn) {
        Map<Position, Piece> initialPieces = new HashMap<>();
        initialPieces.put(from, fromPawn);
        initialPieces.put(to, toPawn);
        return new Board(initialPieces);
    }
}