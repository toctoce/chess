package chess.domain.status;

import chess.domain.board.Board;
import chess.domain.piece.Color;

public class CheckmateDetector {

    private final CheckDetector checkDetector;
    private final MovementValidator movementValidator;

    public CheckmateDetector(CheckDetector checkDetector, MovementValidator movementValidator) {
        this.checkDetector = checkDetector;
        this.movementValidator = movementValidator;
    }

    public boolean isCheckmate(Board board, Color currentColor) {
        return checkDetector.isCheck(board, currentColor) && !movementValidator.anyPieceHasLegalMove(board,
                currentColor);
    }
}