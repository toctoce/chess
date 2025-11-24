package chess.domain.status;

import static chess.common.message.ErrorMessage.PIECE_NOT_FOUND;
import static chess.common.message.ErrorMessage.RULE_FRIENDLY_FIRE;
import static chess.common.message.ErrorMessage.RULE_INVALID_PIECE_MOVE;
import static chess.common.message.ErrorMessage.RULE_KING_IN_CHECK_AFTER_MOVE;
import static chess.common.message.ErrorMessage.RULE_PATH_BLOCKED;
import static chess.common.message.ErrorMessage.RULE_SAME_POSITION_MOVE;
import static chess.common.message.ErrorMessage.RULE_WRONG_TURN_PIECE;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import chess.common.exception.IllegalMoveException;
import chess.common.exception.PieceNotFoundException;
import chess.common.exception.RuleViolationException;
import chess.domain.board.Board;
import chess.domain.board.Position;
import chess.domain.piece.Color;
import chess.domain.piece.Piece;
import chess.domain.piece.Type;
import chess.domain.piece.impls.King;
import chess.domain.piece.impls.Rook;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@DisplayName("MovementValidator 테스트")
class MovementValidatorTest {

    @Mock
    private CheckDetector checkDetector;

    @Mock
    private Board board;

    @InjectMocks
    private MovementValidator movementValidator;

    private final Position from = Position.from("A1");
    private final Position to = Position.from("A2");
    private final Color WHITE_COLOR = Color.WHITE;
    private Piece movingPiece;

    @BeforeEach
    void setUp() {
        movingPiece = mock(Piece.class);
    }

    @Test
    @DisplayName("출발 위치에 기물이 없으면 PieceNotFoundException을 던진다")
    void validatePieceNotFound() {
        when(board.getPiece(from)).thenReturn(null);

        assertThatThrownBy(() -> movementValidator.validate(from, to, board, WHITE_COLOR))
                .isInstanceOf(PieceNotFoundException.class)
                .hasMessageContaining(PIECE_NOT_FOUND.getMessage());
    }

    @ParameterizedTest
    @EnumSource(Color.class)
    @DisplayName("현재 턴과 다른 색상의 기물을 움직이면 RuleViolationException을 던진다")
    void validateWrongTurnPiece(Color color) {
        when(board.getPiece(from)).thenReturn(movingPiece);
        when(movingPiece.getColor()).thenReturn(color);

        assertThatThrownBy(() -> movementValidator.validate(from, to, board, color.opposite()))
                .isInstanceOf(RuleViolationException.class)
                .hasMessageContaining(RULE_WRONG_TURN_PIECE.getMessage());
    }

    @Test
    @DisplayName("제자리 이동 시 RuleViolationException을 던진다")
    void validateSamePositionMove() {
        when(board.getPiece(from)).thenReturn(movingPiece);
        when(movingPiece.getColor()).thenReturn(WHITE_COLOR);

        assertThatThrownBy(() -> movementValidator.validate(from, from, board, WHITE_COLOR))
                .isInstanceOf(RuleViolationException.class)
                .hasMessageContaining(RULE_SAME_POSITION_MOVE.getMessage());
    }

    @Test
    @DisplayName("도착지에 아군 기물이 있으면 RuleViolationException을 던진다")
    void validateFriendlyFire() {
        Piece targetPiece = mock(Piece.class);

        when(board.getPiece(from)).thenReturn(movingPiece);
        when(movingPiece.getColor()).thenReturn(WHITE_COLOR);
        when(board.getPiece(to)).thenReturn(targetPiece);
        when(targetPiece.getColor()).thenReturn(WHITE_COLOR);

        assertThatThrownBy(() -> movementValidator.validate(from, to, board, WHITE_COLOR))
                .isInstanceOf(RuleViolationException.class)
                .hasMessageContaining(RULE_FRIENDLY_FIRE.getMessage());
    }

    @Test
    @DisplayName("기물 고유 이동 규칙에 어긋나면 RuleViolationException을 던진다")
    void validateInvalidPieceMove() {
        when(board.getPiece(from)).thenReturn(movingPiece);
        when(movingPiece.getColor()).thenReturn(WHITE_COLOR);
        when(movingPiece.isMoveValid(any(), any(), any())).thenReturn(false);

        assertThatThrownBy(() -> movementValidator.validate(from, to, board, WHITE_COLOR))
                .isInstanceOf(RuleViolationException.class)
                .hasMessageContaining(RULE_INVALID_PIECE_MOVE.getMessage());
    }

    @Test
    @DisplayName("나이트가 아닌 기물의 경로에 장애물이 있으면 RuleViolationException을 던진다")
    void validatePathBlocked() {
        when(board.getPiece(from)).thenReturn(movingPiece);
        when(movingPiece.getColor()).thenReturn(WHITE_COLOR);
        when(movingPiece.getType()).thenReturn(Type.ROOK);
        when(movingPiece.isMoveValid(any(), any(), any())).thenReturn(true);
        when(board.hasObstacleInPath(any(), any())).thenReturn(true);

        assertThatThrownBy(() -> movementValidator.validate(from, to, board, WHITE_COLOR))
                .isInstanceOf(RuleViolationException.class)
                .hasMessageContaining(RULE_PATH_BLOCKED.getMessage());
    }

    @Test
    @DisplayName("나이트의 경로는 장애물 검사를 건너뛰고 통과한다")
    void validateKnightIgnoresObstacle() {
        when(board.getPiece(from)).thenReturn(movingPiece);
        when(movingPiece.getColor()).thenReturn(WHITE_COLOR);
        when(movingPiece.getType()).thenReturn(Type.KNIGHT);
        when(movingPiece.isMoveValid(any(), any(), any())).thenReturn(true);

        Map<Position, Piece> initialPieces = new HashMap<>();
        initialPieces.put(from, movingPiece);

        when(checkDetector.isCheck(any(), eq(WHITE_COLOR))).thenReturn(false);

        assertThatCode(() -> movementValidator.validate(from, to, board, WHITE_COLOR))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("이동 후 킹이 체크 상태가 되면 IllegalMoveException을 던진다")
    void validateKingInCheckAfterMove() {
        when(board.getPiece(from)).thenReturn(movingPiece);
        when(movingPiece.getColor()).thenReturn(WHITE_COLOR);
        when(movingPiece.isMoveValid(any(), any(), any())).thenReturn(true);

        Board mockedVirtualBoard = mock(Board.class);
        when(board.movePieceVirtually(any(), any())).thenReturn(mockedVirtualBoard);

        when(checkDetector.isCheck(any(Board.class), eq(WHITE_COLOR))).thenReturn(true);

        assertThatThrownBy(() -> movementValidator.validate(from, to, board, WHITE_COLOR))
                .isInstanceOf(IllegalMoveException.class)
                .hasMessageContaining(RULE_KING_IN_CHECK_AFTER_MOVE.getMessage());
    }

    @Nested
    @DisplayName("캐슬링 유효성 검증")
    @MockitoSettings(strictness = Strictness.LENIENT)
    class CastlingTest {

        private final Position kingFrom = Position.from("E1");
        private final Position kingTo = Position.from("G1");
        private final Position rookPosition = Position.from("H1");
        private final Position passThrough = Position.from("F1");

        private Piece king;
        private Piece rook;

        @BeforeEach
        void setUpCastling() {
            king = mock(King.class);
            rook = mock(Rook.class);

            when(king.getColor()).thenReturn(WHITE_COLOR);
            when(king.getType()).thenReturn(Type.KING);

            when(king.isMoveValid(any(), any(), any())).thenReturn(true);

            when(rook.getColor()).thenReturn(WHITE_COLOR);
            when(rook.getType()).thenReturn(Type.ROOK);

            when(board.getPiece(kingFrom)).thenReturn(king);
            when(board.getPiece(rookPosition)).thenReturn(rook);
        }

        @Test
        @DisplayName("모든 조건(첫 이동, 경로 안전, 체크 아님)을 만족하면 캐슬링이 가능하다")
        void validateCastlingSuccess() {
            when(king.isMoved()).thenReturn(false);
            when(rook.isMoved()).thenReturn(false);

            when(board.hasObstacleInPath(kingFrom, rookPosition)).thenReturn(false);
            when(checkDetector.isCheck(board, WHITE_COLOR)).thenReturn(false);
            when(checkDetector.isSquareAttacked(eq(board), eq(passThrough), any())).thenReturn(false);

            assertThatCode(() -> movementValidator.validate(kingFrom, kingTo, board, WHITE_COLOR))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("킹이 이미 움직였으면 캐슬링 불가")
        void failIfKingMoved() {
            when(king.isMoved()).thenReturn(true);

            assertThatThrownBy(() -> movementValidator.validate(kingFrom, kingTo, board, WHITE_COLOR))
                    .isInstanceOf(IllegalMoveException.class);
        }

        @Test
        @DisplayName("룩이 이미 움직였거나 해당 위치에 룩이 없으면 캐슬링 불가")
        void failIfRookMovedOrMissing() {
            when(king.isMoved()).thenReturn(true);
            when(rook.isMoved()).thenReturn(true);

            assertThatThrownBy(() -> movementValidator.validate(kingFrom, kingTo, board, WHITE_COLOR))
                    .isInstanceOf(IllegalMoveException.class);
        }

        @Test
        @DisplayName("킹과 룩 사이 경로에 장애물이 있으면 캐슬링 불가")
        void failIfPathBlocked() {
            when(king.isMoved()).thenReturn(false);
            when(rook.isMoved()).thenReturn(false);

            when(board.hasObstacleInPath(kingFrom, rookPosition)).thenReturn(true);

            assertThatThrownBy(() -> movementValidator.validate(kingFrom, kingTo, board, WHITE_COLOR))
                    .isInstanceOf(IllegalMoveException.class);
        }

        @Test
        @DisplayName("현재 킹이 체크 상태이면 캐슬링 불가")
        void failIfInCheck() {
            when(king.isMoved()).thenReturn(false);
            when(rook.isMoved()).thenReturn(false);

            when(board.hasObstacleInPath(kingFrom, rookPosition)).thenReturn(false);
            when(checkDetector.isCheck(board, WHITE_COLOR)).thenReturn(true);

            assertThatThrownBy(() -> movementValidator.validate(kingFrom, kingTo, board, WHITE_COLOR))
                    .isInstanceOf(IllegalMoveException.class);
        }

        @Test
        @DisplayName("킹이 지나가는 경로(F1)가 공격받고 있으면 캐슬링 불가")
        void failIfPassThroughAttacked() {
            when(king.isMoved()).thenReturn(false);
            when(rook.isMoved()).thenReturn(false);

            when(board.hasObstacleInPath(kingFrom, rookPosition)).thenReturn(false);
            when(checkDetector.isCheck(board, WHITE_COLOR)).thenReturn(false);
            when(checkDetector.isSquareAttacked(eq(board), eq(passThrough), any())).thenReturn(true);

            assertThatThrownBy(() -> movementValidator.validate(kingFrom, kingTo, board, WHITE_COLOR))
                    .isInstanceOf(IllegalMoveException.class);
        }
    }
}