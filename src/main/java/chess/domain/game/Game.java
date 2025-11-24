package chess.domain.game;

import static chess.common.message.ErrorMessage.GAME_ALREADY_FINISHED;

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
            Position from,
            Position to,
            MovementValidator movementValidator,
            StatusCalculator statusCalculator
    ) {
        if (isFinished()) {
            throw new GameFinishedException(GAME_ALREADY_FINISHED.getMessage(status.getDescription()));
        }

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

    public void undo() {
        BoardSnapshot previousBoard = history.undoHistory(board, currentTurn);

        board.restore(previousBoard.pieces());
        this.currentTurn = previousBoard.turn();
        this.status = GameStatus.ONGOING;
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