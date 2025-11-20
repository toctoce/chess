package chess.domain.state.impls;

import chess.domain.board.Board;
import chess.domain.board.Position;
import chess.domain.piece.Color;
import chess.domain.rule.CheckDetector;
import chess.domain.rule.RuleValidator;
import chess.domain.state.GameState;
import chess.domain.state.GameStatus;

public class OngoingState implements GameState {

    private final RuleValidator ruleValidator;
    private final CheckDetector checkDetector;

    public OngoingState(RuleValidator ruleValidator, CheckDetector checkDetector) {
        this.ruleValidator = ruleValidator;
        this.checkDetector = checkDetector;
    }

    // todo: 스테일메이트 검사, 50수 검사, 3회반복 검사
    @Override
    public GameState move(Position from, Position to, Board board, Color turnColor) {
        ruleValidator.validate(from, to, board, turnColor);
        board.movePiece(from, to);

        Color opponentColor = turnColor.opposite();
        if (checkDetector.isCheckmate(board, opponentColor)) {
            return new CheckmateState(turnColor);
        }

//        if (checkDetector.isStalemate(board, opponentColor)) {
//            return new StalemateState();
//        }

//         if (fiftyMoveRuleDetector.isSatisfied(gameHistory)) {
//             return new FiftyMoveRuleDrawState();
//         }

//        if (repetitionDetector.isRepetitionSatisfied(game.getCurrentPositionHash())) {
//            return new RepetitionDrawState(); // 새로운 DrawState 구현체 필요
//        }

        return this;
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public GameStatus status() {
        return GameStatus.ONGOING;
    }
}