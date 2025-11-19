package chess.domain.rule;

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
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("RuleValidator 테스트")
class RuleValidatorTest {

    @Mock
    private CheckDetector checkDetector;

    @Mock
    private Board board;

    @InjectMocks
    private RuleValidator ruleValidator;

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

        assertThatThrownBy(() -> ruleValidator.validate(from, to, board, WHITE_COLOR))
                .isInstanceOf(PieceNotFoundException.class)
                .hasMessageContaining(PIECE_NOT_FOUND.getMessage());
    }

    @ParameterizedTest
    @EnumSource(Color.class)
    @DisplayName("현재 턴과 다른 색상의 기물을 움직이면 RuleViolationException을 던진다")
    void validateWrongTurnPiece(Color color) {
        when(board.getPiece(from)).thenReturn(movingPiece);
        when(movingPiece.getColor()).thenReturn(color);

        assertThatThrownBy(() -> ruleValidator.validate(from, to, board, color.opposite()))
                .isInstanceOf(RuleViolationException.class)
                .hasMessageContaining(RULE_WRONG_TURN_PIECE.getMessage());
    }

    @Test
    @DisplayName("제자리 이동 시 RuleViolationException을 던진다")
    void validateSamePositionMove() {
        when(board.getPiece(from)).thenReturn(movingPiece);
        when(movingPiece.getColor()).thenReturn(WHITE_COLOR);

        assertThatThrownBy(() -> ruleValidator.validate(from, from, board, WHITE_COLOR))
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

        assertThatThrownBy(() -> ruleValidator.validate(from, to, board, WHITE_COLOR))
                .isInstanceOf(RuleViolationException.class)
                .hasMessageContaining(RULE_FRIENDLY_FIRE.getMessage());
    }

    @Test
    @DisplayName("기물 고유 이동 규칙에 어긋나면 RuleViolationException을 던진다")
    void validateInvalidPieceMove() {
        when(board.getPiece(from)).thenReturn(movingPiece);
        when(movingPiece.getColor()).thenReturn(WHITE_COLOR);
        when(movingPiece.isMoveValid(any(), any(), any())).thenReturn(false);

        assertThatThrownBy(() -> ruleValidator.validate(from, to, board, WHITE_COLOR))
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

        assertThatThrownBy(() -> ruleValidator.validate(from, to, board, WHITE_COLOR))
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
        when(board.getPieces()).thenReturn(initialPieces);

        when(checkDetector.isKingInCheck(any(), eq(WHITE_COLOR))).thenReturn(false);

        assertThatCode(() -> ruleValidator.validate(from, to, board, WHITE_COLOR))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("이동 후 킹이 체크 상태가 되면 IllegalMoveException을 던진다")
    void validateKingInCheckAfterMove() {
        when(board.getPiece(from)).thenReturn(movingPiece);
        when(movingPiece.getColor()).thenReturn(WHITE_COLOR);
        when(movingPiece.isMoveValid(any(), any(), any())).thenReturn(true);

        Map<Position, Piece> initialPieces = new HashMap<>();
        initialPieces.put(from, movingPiece);
        when(board.getPieces()).thenReturn(initialPieces);

        when(checkDetector.isKingInCheck(any(Board.class), eq(WHITE_COLOR))).thenReturn(true);

        assertThatThrownBy(() -> ruleValidator.validate(from, to, board, WHITE_COLOR))
                .isInstanceOf(IllegalMoveException.class)
                .hasMessageContaining(RULE_KING_IN_CHECK_AFTER_MOVE.getMessage());
    }
}