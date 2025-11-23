package chess.domain.piece;

public enum Type {
    KING, QUEEN, ROOK, BISHOP, KNIGHT, PAWN;

    public boolean isMajor() {
        return this == QUEEN || this == ROOK;
    }

    public boolean isMinor() {
        return this == BISHOP || this == KNIGHT;
    }
}