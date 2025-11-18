package chess.domain.piece;

public enum Color {
    WHITE(1, 1, 0),
    BLACK(-1, 6, 7);

    private final int direction;
    private final int pawnStartRank;
    private final int pieceStartRank;

    Color(int direction, int pawnStartRank, int pieceStartRank) {
        this.direction = direction;
        this.pawnStartRank = pawnStartRank;
        this.pieceStartRank = pieceStartRank;
    }

    public Color opposite() {
        if (this == WHITE) {
            return BLACK;
        }
        return WHITE;
    }

    public int getDirection() {
        return direction;
    }
    
    public int getPieceStartRank() {
        return pieceStartRank;
    }

    public int getPawnStartRank() {
        return pawnStartRank;
    }
}
