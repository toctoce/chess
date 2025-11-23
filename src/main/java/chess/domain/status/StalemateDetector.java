package chess.domain.status;

import chess.domain.board.Board;
import chess.domain.piece.Color;

public class StalemateDetector {

    private final RuleValidator ruleValidator;
    private final CheckDetector checkDetector;

    public StalemateDetector(RuleValidator ruleValidator, CheckDetector checkDetector) {
        this.ruleValidator = ruleValidator;
        this.checkDetector = checkDetector;
    }

    public boolean isStalemate(Board board, Color currentColor) {
        return !checkDetector.isCheck(board, currentColor) && !ruleValidator.anyPieceHasLegalMove(board, currentColor);
    }
}