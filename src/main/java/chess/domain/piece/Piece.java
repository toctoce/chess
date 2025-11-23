package chess.domain.piece;

import static chess.common.message.ErrorMessage.PIECE_INVALID_CREATION_ARGUMENTS;

import chess.common.exception.PieceCreationException;
import chess.domain.board.Board;
import chess.domain.board.Position;
import chess.domain.strategy.MovementStrategy;

public abstract class Piece {

    private final Color color;
    private final Type type;

    protected final MovementStrategy movementStrategy;

    public Piece(Color color, Type type, MovementStrategy movementStrategy) {
        if (color == null || type == null || movementStrategy == null) {
            throw new PieceCreationException(PIECE_INVALID_CREATION_ARGUMENTS.getMessage());
        }
        this.color = color;
        this.type = type;
        this.movementStrategy = movementStrategy;
    }

    public boolean isMoveValid(Position from, Position to, Board board) {
        if (from.equals(to)) {
            return false;
        }

        return movementStrategy.isMoveValid(from, to, board);
    }

    public boolean isMajor() {
        return type.isMajor();
    }

    public boolean isMinor() {
        return type.isMinor();
    }

    public abstract String getSymbol();

    public Color getColor() {
        return color;
    }

    public Type getType() {
        return type;
    }
}