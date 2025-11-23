package chess.domain.status;

import chess.domain.board.Board;
import chess.domain.piece.Color;

public class CheckmateDetector {

    private final CheckDetector checkDetector;
    private final RuleValidator ruleValidator;

    public CheckmateDetector(CheckDetector checkDetector, RuleValidator ruleValidator) {
        this.checkDetector = checkDetector;
        this.ruleValidator = ruleValidator;
    }

    public boolean isCheckmate(Board board, Color currentColor) {
        return checkDetector.isCheck(board, currentColor) && !ruleValidator.anyPieceHasLegalMove(board, currentColor);
    }
}