package chess.domain.board;

import static chess.common.message.ErrorMessage.PIECE_NOT_FOUND;

import chess.common.exception.PieceNotFoundException;
import chess.domain.factory.PieceFactory;
import chess.domain.factory.impls.BlackPieceFactory;
import chess.domain.factory.impls.WhitePieceFactory;
import chess.domain.piece.Color;
import chess.domain.piece.Piece;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

public class Board {

    private final Map<Position, Piece> pieces;

    public Board(Map<Position, Piece> initialPieces) {
        this.pieces = new HashMap<>(initialPieces);
    }

    public Board() {
        this(new HashMap<>());
    }

    public void initialize() {
        pieces.clear();

        PieceFactory whiteFactory = new WhitePieceFactory();
        PieceFactory blackFactory = new BlackPieceFactory();

        placePieces(whiteFactory, Color.WHITE);
        placePieces(blackFactory, Color.BLACK);
    }

    private void placePieces(PieceFactory factory, Color color) {
        int backRank = color.getPieceStartRank();
        int pawnRank = color.getPawnStartRank();

        placePiece(factory.createRook(), Position.of(0, backRank));
        placePiece(factory.createKnight(), Position.of(1, backRank));
        placePiece(factory.createBishop(), Position.of(2, backRank));
        placePiece(factory.createQueen(), Position.of(3, backRank));
        placePiece(factory.createKing(), Position.of(4, backRank));
        placePiece(factory.createBishop(), Position.of(5, backRank));
        placePiece(factory.createKnight(), Position.of(6, backRank));
        placePiece(factory.createRook(), Position.of(7, backRank));

        IntStream.rangeClosed(0, 7)
                .forEach(i -> placePiece(factory.createPawn(), Position.of(i, pawnRank)));
    }

    public void movePiece(Position from, Position to) {
        Piece piece = getPiece(from);
        if (piece == null) {
            throw new PieceNotFoundException(PIECE_NOT_FOUND.getMessage());
        }

        // todo: 만약 경로 방해나 고유 규칙 위반이 있다면, Board를 호출하기 전 Service/Validator 레벨에서 검증해야 함.
        pieces.remove(from);
        placePiece(piece, to);
    }

    private void placePiece(Piece piece, Position position) {
        pieces.put(position, piece);
    }

    public Piece getPiece(Position position) {
        return pieces.get(position);
    }

    public boolean hasObstacleInPath(Position from, Position to) {
        int xDiff = to.x() - from.x();
        int yDiff = to.y() - from.y();

        boolean isStraight = (xDiff == 0 && yDiff != 0) || (xDiff != 0 && yDiff == 0);
        boolean isDiagonal = (Math.abs(xDiff) == Math.abs(yDiff) && xDiff != 0);

        if (!isStraight && !isDiagonal) {
            return false;
        }

        int stepX = Integer.compare(xDiff, 0);
        int stepY = Integer.compare(yDiff, 0);

        int distance = Math.max(Math.abs(xDiff), Math.abs(yDiff));

        return IntStream.range(1, distance)
                .mapToObj(index -> {
                    int currentX = from.x() + (index * stepX);
                    int currentY = from.y() + (index * stepY);
                    return Position.of(currentX, currentY);
                })
                .anyMatch(pieces::containsKey);
    }

    public Map<Position, Piece> getPieces() {
        return Collections.unmodifiableMap(pieces);
    }
}