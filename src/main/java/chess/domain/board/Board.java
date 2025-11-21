package chess.domain.board;

import static chess.common.message.ErrorMessage.PIECE_NOT_FOUND;

import chess.common.exception.PieceNotFoundException;
import chess.domain.factory.PieceFactory;
import chess.domain.factory.impls.BlackPieceFactory;
import chess.domain.factory.impls.WhitePieceFactory;
import chess.domain.piece.Color;
import chess.domain.piece.Piece;
import chess.domain.piece.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Board {

    private final Map<Position, Piece> pieces;

    public Board(Map<Position, Piece> initialPieces) {
        this.pieces = new HashMap<>(initialPieces);
    }

    public Board() {
        this(new HashMap<>());
    }

    public Board(Board board) {
        this(new HashMap<>(board.pieces));
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

        pieces.remove(from);
        placePiece(piece, to);
    }

    public Board movePieceVirtually(Position from, Position to) {
        Board virtualBoard = new Board(this.pieces);

        virtualBoard.movePiece(from, to);

        return virtualBoard;
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
                .mapToObj(i -> {
                    int currentX = from.x() + (i * stepX);
                    int currentY = from.y() + (i * stepY);
                    return Position.of(currentX, currentY);
                })
                .anyMatch(pieces::containsKey);
    }

    public Map<Position, Piece> getPieces() {
        return Collections.unmodifiableMap(pieces);
    }

    public Map<Position, Piece> getPiecesByTeam(Color color) {
        return pieces.entrySet().stream()
                .filter(entry -> entry.getValue().getColor() == color)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public List<Position> findPositions(Color color, Type type) {
        return getPiecesByTeam(color).entrySet().stream()
                .filter(entry -> entry.getValue().getType() == type)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public Position findKingPosition(Color color) {
        return findPositions(color, Type.KING).stream()
                .findFirst()
                .orElse(null);
    }

    public void restore(Map<Position, Piece> snapshot) {
        this.pieces.clear();
        this.pieces.putAll(snapshot);
    }
}