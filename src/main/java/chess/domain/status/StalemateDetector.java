package chess.domain.status;

import chess.domain.board.Board;
import chess.domain.game.Game;
import chess.domain.piece.Color;

public class StalemateDetector {

    private final MovementValidator movementValidator;
    private final CheckDetector checkDetector;

    public StalemateDetector(MovementValidator movementValidator, CheckDetector checkDetector) {
        this.movementValidator = movementValidator;
        this.checkDetector = checkDetector;
    }

    public boolean isStalemate(Game game) {
        Board board = game.getBoard();
        Color currentTurn = game.getCurrentTurn();
        return !checkDetector.isCheck(board, currentTurn) &&
                !movementValidator.anyPieceHasLegalMove(board, currentTurn);
    }
}