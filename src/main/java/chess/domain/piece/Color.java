package chess.domain.piece;

public enum Color {
    WHITE(1, 1, 0, 7),
    BLACK(-1, 6, 7, 0);

    private final int direction;
    private final int pawnStartRank;
    private final int pieceStartRank;
    private final int pawnPromotionRank;

    Color(int direction, int pawnStartRank, int pieceStartRank, int pawnPromotionRank) {
        this.direction = direction;
        this.pawnStartRank = pawnStartRank;
        this.pieceStartRank = pieceStartRank;
        this.pawnPromotionRank = pawnPromotionRank;
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
    
    public int getPawnPromotionRank() {
        return pawnPromotionRank;
    }
}
