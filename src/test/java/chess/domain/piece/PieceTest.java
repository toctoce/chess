package chess.domain.piece;

import static org.assertj.core.api.Assertions.assertThat;

import chess.domain.piece.impls.Bishop;
import chess.domain.piece.impls.King;
import chess.domain.piece.impls.Knight;
import chess.domain.piece.impls.Pawn;
import chess.domain.piece.impls.Queen;
import chess.domain.piece.impls.Rook;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Piece 객체 테스트")
class PieceTest {

    @Nested
    @DisplayName("기물 분류 테스트")
    class PieceClassificationTest {

        @Test
        @DisplayName("룩과 퀸은 주기물이다")
        void rookAndQueenAreMajorPieces() {
            Piece rook = new Rook(Color.WHITE);
            Piece queen = new Queen(Color.WHITE);

            assertThat(rook.isMajor()).isTrue();
            assertThat(queen.isMajor()).isTrue();

            assertThat(rook.isMinor()).isFalse();
            assertThat(queen.isMinor()).isFalse();
        }

        @Test
        @DisplayName("비숍과 나이트는 부기물이다")
        void bishopAndKnightAreMinorPieces() {
            Piece bishop = new Bishop(Color.WHITE);
            Piece knight = new Knight(Color.WHITE);

            assertThat(bishop.isMinor()).isTrue();
            assertThat(knight.isMinor()).isTrue();

            assertThat(bishop.isMajor()).isFalse();
            assertThat(knight.isMajor()).isFalse();
        }

        @Test
        @DisplayName("킹과 폰은 주기물도 부기물도 아니다")
        void kingAndPawnAreNeitherMajorNorMinor() {
            Piece king = new King(Color.WHITE);
            Piece pawn = new Pawn(Color.WHITE);

            assertThat(king.isMajor()).isFalse();
            assertThat(king.isMinor()).isFalse();

            assertThat(pawn.isMajor()).isFalse();
            assertThat(pawn.isMinor()).isFalse();
        }
    }
}