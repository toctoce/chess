package chess.domain.game;

import static chess.common.message.ErrorMessage.GAME_ALREADY_FINISHED;
import static chess.common.message.ErrorMessage.PLAYER_CAN_NOT_UNDO;
import static chess.common.message.ErrorMessage.PLAYER_INVALID_TURN;

import chess.common.exception.ChessException;
import chess.common.exception.GameFinishedException;
import chess.domain.board.Board;
import chess.domain.board.Position;
import chess.domain.piece.Color;
import chess.domain.piece.Piece;
import chess.domain.piece.Type;
import chess.domain.status.GameStatus;
import chess.domain.status.MovementValidator;
import chess.domain.status.StatusCalculator;

public class Game {

    private Long id;
    private final Board board;
    private final GameHistory history;

    private Color currentTurn;
    private GameStatus status;

    private Player whitePlayer;
    private Player blackPlayer;

    public void join(Player player) {
        if (whitePlayer == null) {
            this.whitePlayer = player;
            return;
        }
        if (player.equals(whitePlayer)) {
            throw new ChessException("이미 게임에 참여 중입니다.");
        }

        if (blackPlayer == null) {
            this.blackPlayer = player;
            return;
        }
        throw new ChessException("게임 인원이 꽉 찼습니다.");
    }

    public Game(Board board) {
        this.board = board;
        this.currentTurn = Color.WHITE;
        this.status = GameStatus.ONGOING;
        this.history = new GameHistory();

        this.history.updateHistory(board, this.currentTurn, true);
    }

    public Game(Long id, Board board, Color currentTurn, GameStatus status, GameHistory history) {
        this.id = id;
        this.board = board;
        this.currentTurn = currentTurn;
        this.status = status;
        this.history = history;
    }

    public void move(
            Player player,
            Position from,
            Position to,
            MovementValidator movementValidator,
            StatusCalculator statusCalculator
    ) {
        if (isFinished()) {
            throw new GameFinishedException(GAME_ALREADY_FINISHED.getMessage(status.getDescription()));
        }

        validatePlayerTurn(player);

        movementValidator.validate(from, to, board, currentTurn);

        Piece movedPiece = board.getPiece(from);
        Piece targetPiece = board.getPiece(to);

        history.saveHistory(board, currentTurn);

        board.move(from, to);

        switchTurn();

        boolean isFiftyMoveReset = (movedPiece.getType() == Type.PAWN || targetPiece != null);
        history.updateHistory(board, currentTurn, isFiftyMoveReset);

        this.status = statusCalculator.calculateNextStatus(this);
    }

    private void validatePlayerTurn(Player player) {
        if (currentTurn == Color.WHITE && (whitePlayer == null || !whitePlayer.equals(player))) {
            throw new ChessException(PLAYER_INVALID_TURN.getMessage(String.valueOf(Color.WHITE)));
        }
        if (currentTurn == Color.BLACK && (blackPlayer == null || !blackPlayer.equals(player))) {
            throw new ChessException(PLAYER_INVALID_TURN.getMessage(String.valueOf(Color.BLACK)));
        }
    }

    public void undo(Player player) {
        validateUndoPermission(player);

        BoardSnapshot previousBoardSnapshot = history.undoHistory(board, currentTurn);

        board.restore(previousBoardSnapshot);
        this.currentTurn = previousBoardSnapshot.turn();
        this.status = GameStatus.ONGOING;
    }

    private void validateUndoPermission(Player player) {
        if (currentTurn == Color.WHITE) {
            if (blackPlayer == null || !blackPlayer.equals(player)) {
                throw new ChessException(PLAYER_CAN_NOT_UNDO.getMessage());
            }
        }
        if (currentTurn == Color.BLACK) {
            if (whitePlayer == null || !whitePlayer.equals(player)) {
                throw new ChessException(PLAYER_CAN_NOT_UNDO.getMessage());
            }
        }
    }

    private void switchTurn() {
        this.currentTurn = this.currentTurn.opposite();
    }

    public boolean isFinished() {
        return status != GameStatus.ONGOING;
    }

    public Long getId() {
        return id;
    }

    public Board getBoard() {
        return board;
    }

    public Color getCurrentTurn() {
        return currentTurn;
    }

    public GameStatus getStatus() {
        return status;
    }

    public GameHistory getHistory() {
        return history;
    }
}