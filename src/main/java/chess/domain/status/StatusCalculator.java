package chess.domain.status;

import chess.domain.board.Board;
import chess.domain.game.GameHistory;
import chess.domain.piece.Color;

public class StatusCalculator {

    private final CheckmateDetector checkmateDetector;
    private final StalemateDetector stalemateDetector;
    private final FiftyMoveDetector fiftyMoveDetector;
    // private final RepetitionDetector repetitionDetector;
    // private final InsufficientMaterialDetector insufficientMaterialDetector;

    public StatusCalculator(
            CheckmateDetector checkmateDetector,
            StalemateDetector stalemateDetector,
            FiftyMoveDetector fiftyMoveDetector
    ) {
        this.checkmateDetector = checkmateDetector;
        this.stalemateDetector = stalemateDetector;
        this.fiftyMoveDetector = fiftyMoveDetector;
    }

    public GameStatus calculateNextStatus(Board board, Color nextTurn, GameHistory history) {
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

        // if (repetitionDetector.isSatisfied(history)) {
        //     return GameStatus.REPETITION_DRAW;
        // }

        // if (insufficientMaterialDetector.isInsufficient(board)) {
        //     return GameStatus.INSUFFICIENT_MATERIAL_DRAW;
        // }

        return GameStatus.ONGOING;
    }
}