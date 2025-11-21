package chess.domain.rule;

import chess.domain.board.Board;
import chess.domain.board.Position;
import chess.domain.game.GameHistory;
import chess.domain.piece.Color;

public class ChessArbiter {

    private final RuleValidator ruleValidator;
    private final CheckmateDetector checkmateDetector;
    private final StalemateDetector stalemateDetector;
    // (아직 파일 트리에 없지만 필요한 디텍터들)
    // private final FiftyMoveRuleDetector fiftyMoveRuleDetector;
    // private final RepetitionDetector repetitionDetector;
    // private final InsufficientMaterialDetector insufficientMaterialDetector;

    public ChessArbiter(RuleValidator ruleValidator,
                        CheckmateDetector checkmateDetector,
                        StalemateDetector stalemateDetector) {
        this.ruleValidator = ruleValidator;
        this.checkmateDetector = checkmateDetector;
        this.stalemateDetector = stalemateDetector;
    }

    public void validateMove(Position from, Position to, Board board, Color turnColor) {
        ruleValidator.validate(from, to, board, turnColor);
    }

    public GameStatus calculateNextStatus(Board board, Color nextTurn, GameHistory history) {
        // 1. 체크메이트 검사 (승리)
        if (checkmateDetector.isCheckmate(board, nextTurn)) {
            if (nextTurn == Color.WHITE) {
                return GameStatus.CHECKMATE_BLACK_WIN;
            }
            return GameStatus.CHECKMATE_WHITE_WIN;
        }

        if (stalemateDetector.isStalemate(board, nextTurn)) {
            return GameStatus.STALEMATE_DRAW;
        }

        // if (fiftyMoveRuleDetector.isSatisfied(history)) {
        //     return GameStatus.FIFTY_MOVE_RULE_DRAW;
        // }

        // if (repetitionDetector.isSatisfied(history)) {
        //     return GameStatus.REPETITION_DRAW;
        // }

        // if (insufficientMaterialDetector.isInsufficient(board)) {
        //     return GameStatus.INSUFFICIENT_MATERIAL_DRAW;
        // }

        return GameStatus.ONGOING;
    }
}