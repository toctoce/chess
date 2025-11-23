package chess.domain.status;

import chess.domain.game.Game;
import chess.domain.piece.Color;

public class StatusCalculator {

    private final CheckmateDetector checkmateDetector;
    private final StalemateDetector stalemateDetector;
    private final FiftyMoveDetector fiftyMoveDetector;
    private final RepetitionDetector repetitionDetector;
    private final InsufficientMaterialDetector insufficientMaterialDetector;

    public StatusCalculator(
            CheckmateDetector checkmateDetector,
            StalemateDetector stalemateDetector,
            FiftyMoveDetector fiftyMoveDetector,
            RepetitionDetector repetitionDetector,
            InsufficientMaterialDetector insufficientMaterialDetector
    ) {
        this.checkmateDetector = checkmateDetector;
        this.stalemateDetector = stalemateDetector;
        this.fiftyMoveDetector = fiftyMoveDetector;
        this.repetitionDetector = repetitionDetector;
        this.insufficientMaterialDetector = insufficientMaterialDetector;
    }

    public GameStatus calculateNextStatus(Game game) {
        if (checkmateDetector.isCheckmate(game)) {
            Color nextTurn = game.getCurrentTurn().opposite();
            if (nextTurn == Color.WHITE) {
                return GameStatus.CHECKMATE_BLACK_WIN;
            }
            return GameStatus.CHECKMATE_WHITE_WIN;
        }

        if (stalemateDetector.isStalemate(game)) {
            return GameStatus.STALEMATE_DRAW;
        }

        if (fiftyMoveDetector.isFiftyMove(game)) {
            return GameStatus.FIFTY_MOVE_RULE_DRAW;
        }

        if (repetitionDetector.isRepetition(game)) {
            return GameStatus.REPETITION_DRAW;
        }

        if (insufficientMaterialDetector.isInsufficientMaterial(game)) {
            return GameStatus.INSUFFICIENT_MATERIAL_DRAW;
        }

        return GameStatus.ONGOING;
    }
}