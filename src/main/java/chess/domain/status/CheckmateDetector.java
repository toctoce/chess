package chess.domain.status;

import chess.domain.board.Board;
import chess.domain.game.Game;
import chess.domain.piece.Color;

public class CheckmateDetector {

    private final CheckDetector checkDetector;
    private final MovementValidator movementValidator;

    public CheckmateDetector(CheckDetector checkDetector, MovementValidator movementValidator) {
        this.checkDetector = checkDetector;
        this.movementValidator = movementValidator;
    }

    public boolean isCheckmate(Game game) {
        Board board = game.getBoard();
        Color currentTurn = game.getCurrentTurn();
        return checkDetector.isCheck(board, currentTurn) &&
                !movementValidator.anyPieceHasLegalMove(board, currentTurn);
    }
}