package chess.domain.game;

import static chess.common.message.ErrorMessage.NO_HISTORY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import chess.common.exception.EmptyHistoryException;
import chess.common.exception.GameFinishedException;
import chess.domain.board.Board;
import chess.domain.board.Position;
import chess.domain.piece.Color;
import chess.domain.piece.impls.Rook;
import chess.domain.status.GameStatus;
import chess.domain.status.MovementValidator;
import chess.domain.status.StatusCalculator;
import java.util.HashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

class GameTest {

    private Game game;
    private Board board;
    private GameHistory history;
    private MovementValidator validator;
    private StatusCalculator calculator;

    @BeforeEach
    void setUp() {
        board = mock(Board.class);
        history = mock(GameHistory.class);
        validator = mock(MovementValidator.class);
        calculator = mock(StatusCalculator.class);

        game = new Game(1L, board, Color.WHITE, GameStatus.ONGOING, history);
    }

    @Test
    @DisplayName("move 실행 시 검증 -> 저장 -> 이동 -> 업데이트 -> 판정 순서로 실행된다")
    void moveExecutesInCorrectOrder() {
        Position from = Position.from("A1");
        Position to = Position.from("A2");

        when(board.getPiece(from)).thenReturn(new Rook(Color.WHITE));
        when(calculator.calculateNextStatus(game)).thenReturn(GameStatus.ONGOING);

        game.move(from, to, validator, calculator);

        InOrder inOrder = inOrder(validator, history, board, calculator);

        inOrder.verify(validator).validate(from, to, board, Color.WHITE);
        inOrder.verify(history).saveHistory(board, Color.WHITE);
        inOrder.verify(board).movePiece(from, to);
        inOrder.verify(history).updateHistory(eq(board), eq(Color.BLACK), any(Boolean.class));
        inOrder.verify(calculator).calculateNextStatus(game);

        assertThat(game.getCurrentTurn()).isEqualTo(Color.BLACK);
    }

    @Test
    @DisplayName("이미 종료된 게임에서 move를 시도하면 예외를 던진다")
    void moveThrowExceptionIfFinished() {
        game = new Game(1L, board, Color.WHITE, GameStatus.CHECKMATE_WHITE_WIN, history);

        assertThatThrownBy(() -> game.move(Position.from("A1"), Position.from("A2"), validator, calculator))
                .isInstanceOf(GameFinishedException.class);
    }

    @Test
    @DisplayName("undo 실행 시 이전 상태를 복원하고 턴을 되돌린다")
    void undoRestoresState() {
        BoardSnapshot snapshot = new BoardSnapshot(new HashMap<>(), Color.WHITE);
        when(history.undoHistory(board, Color.WHITE)).thenReturn(snapshot);

        game.undo();

        verify(board).restore(any());
        assertThat(game.getCurrentTurn()).isEqualTo(Color.WHITE);
        assertThat(game.getStatus()).isEqualTo(GameStatus.ONGOING);
    }

    @Test
    @DisplayName("기록이 없는데 undo를 시도하면 예외를 던진다")
    void undoThrowsExceptionIfEmpty() {
        when(history.undoHistory(any(), any()))
                .thenThrow(new EmptyHistoryException(NO_HISTORY.getMessage()));
        
        assertThatThrownBy(() -> game.undo())
                .isInstanceOf(EmptyHistoryException.class);
    }
}