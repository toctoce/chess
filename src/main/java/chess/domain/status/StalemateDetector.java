package chess.domain.status;

import chess.domain.board.Board;
import chess.domain.piece.Color;

public class StalemateDetector {

    private final MovementValidator movementValidator;
    private final CheckDetector checkDetector;

    public StalemateDetector(MovementValidator movementValidator, CheckDetector checkDetector) {
        this.movementValidator = movementValidator;
        this.checkDetector = checkDetector;
    }

    public boolean isStalemate(Board board, Color currentColor) {
        return !checkDetector.isCheck(board, currentColor) && !movementValidator.anyPieceHasLegalMove(board,
                currentColor);
    }
}