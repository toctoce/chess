package chess.domain.status;

import chess.domain.board.Board;
import chess.domain.game.Game;
import chess.domain.game.GameHistory;
import chess.domain.piece.Color;

public class StatusCalculator {

    private final CheckmateDetector checkmateDetector;
    private final StalemateDetector stalemateDetector;
    private final FiftyMoveDetector fiftyMoveDetector;
    private final RepetitionDetector repetitionDetector;
    // private final InsufficientMaterialDetector insufficientMaterialDetector;

    public StatusCalculator(
            CheckmateDetector checkmateDetector,
            StalemateDetector stalemateDetector,
            FiftyMoveDetector fiftyMoveDetector,
            RepetitionDetector repetitionDetector
    ) {
        this.checkmateDetector = checkmateDetector;
        this.stalemateDetector = stalemateDetector;
        this.fiftyMoveDetector = fiftyMoveDetector;
        this.repetitionDetector = repetitionDetector;
    }

    public GameStatus calculateNextStatus(Game game) {
        Board board = game.getBoard();
        Color nextTurn = game.getCurrentTurn().opposite();
        GameHistory history = game.getHistory();

        if (checkmateDetector.isCheckmate(board, nextTurn)) {
            if (nextTurn == Color.WHITE) {
                return GameStatus.CHECKMATE_BLACK_WIN;
            }
            return GameStatus.CHECKMATE_WHITE_WIN;
        }

        if (stalemateDetector.isStalemate(board, nextTurn)) {
            return GameStatus.STALEMATE_DRAW;
        }

        if (fiftyMoveDetector.isFiftyMove(history)) {
            return GameStatus.FIFTY_MOVE_RULE_DRAW;
        }

        if (repetitionDetector.isRepetition(game)) {
            return GameStatus.REPETITION_DRAW;
        }

        // if (insufficientMaterialDetector.isInsufficient(board)) {
        //     return GameStatus.INSUFFICIENT_MATERIAL_DRAW;
        // }

        return GameStatus.ONGOING;
    }
}