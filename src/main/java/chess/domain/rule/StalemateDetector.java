package chess.domain.rule;

import chess.domain.board.Board;
import chess.domain.board.Position;
import chess.domain.piece.Color;
import chess.domain.piece.Piece;
import java.util.stream.IntStream;

public class StalemateDetector {

    private final RuleValidator ruleValidator;
    private final CheckDetector checkDetector;

    public StalemateDetector(RuleValidator ruleValidator, CheckDetector checkDetector) {
        this.ruleValidator = ruleValidator;
        this.checkDetector = checkDetector;
    }

    public boolean isStalemate(Board board, Color currentColor) {
        return !checkDetector.isCheck(board, currentColor) && !anyPieceHasLegalMove(board, currentColor);
    }

    private boolean anyPieceHasLegalMove(Board board, Color currentColor) {
        return board.getPiecesByTeam(currentColor).entrySet().stream()
                .anyMatch(entry -> {
                    Position from = entry.getKey();
                    Piece piece = entry.getValue();

                    return pieceHasLegalMove(from, board, currentColor);
                });
    }

    private boolean pieceHasLegalMove(Position from, Board board, Color currentColor) {
        return IntStream.rangeClosed(0, 7)
                .anyMatch(x -> IntStream.rangeClosed(0, 7)
                        .anyMatch(y -> {
                            Position to = Position.of(x, y);

                            return ruleValidator.isLegalMove(from, to, board, currentColor);
                        })
                );
    }
}