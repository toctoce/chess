package chess.domain.board;

import static chess.common.message.ErrorMessage.PIECE_NOT_FOUND;

import chess.common.exception.PieceNotFoundException;
import chess.domain.factory.PieceFactory;
import chess.domain.factory.impls.BlackPieceFactory;
import chess.domain.factory.impls.WhitePieceFactory;
import chess.domain.game.BoardSnapshot;
import chess.domain.piece.Color;
import chess.domain.piece.Piece;
import chess.domain.piece.Type;
import chess.domain.piece.impls.Queen;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Board {

    private final Map<Position, Piece> pieces;
    private Position enPassantTarget;

    public Board(Map<Position, Piece> initialPieces) {
        this.pieces = new HashMap<>(initialPieces);
        enPassantTarget = null;
    }

    public Board() {
        this(new HashMap<>());
    }

    public Board(Board board) {
        this.pieces = new HashMap<>(board.pieces);
        this.enPassantTarget = board.enPassantTarget;
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

        removePiece(from);
        placePiece(piece.afterMove(), to);
    }

    public void move(Position from, Position to) {
        Piece piece = getPiece(from);
        if (piece == null) {
            throw new PieceNotFoundException(PIECE_NOT_FOUND.getMessage());
        }

        if (isCastling(from, to, piece)) {
            castling(from, to);
            return;
        }

        if (isEnPassant(to, piece)) {
            enPassant(from, to);
            return;
        }

        movePiece(from, to);
        updateAfterMove(from, to, piece);
    }

    private void updateAfterMove(Position from, Position to, Piece piece) {
        updateEnPassantTarget(piece, from, to);
        if (isPromotion(to, piece)) {
            promotion(to, piece.getColor());
        }
    }

    private boolean isCastling(Position from, Position to, Piece piece) {
        return piece.getType() == Type.KING && Math.abs(from.x() - to.x()) == 2;
    }

    private void castling(Position kingFrom, Position kingTo) {
        int direction;
        Position rookFrom, rookTo;

        if (kingTo.x() - kingFrom.x() > 0) {
            direction = 1;
            rookFrom = Position.of(7, kingFrom.y());
            rookTo = Position.of(kingFrom.x() + direction, kingFrom.y());
        } else {
            direction = -1;
            rookFrom = Position.of(0, kingFrom.y());
            rookTo = Position.of(kingFrom.x() + direction, kingFrom.y());
        }

        movePiece(kingFrom, kingTo);
        movePiece(rookFrom, rookTo);
    }

    private boolean isEnPassant(Position to, Piece piece) {
        return piece.getType() == Type.PAWN && to.equals(enPassantTarget);
    }

    private void enPassant(Position from, Position to) {
        movePiece(from, to);

        Position capturedPawnPos = Position.of(to.x(), from.y());
        removePiece(capturedPawnPos);
    }

    private void updateEnPassantTarget(Piece piece, Position from, Position to) {
        if (piece.getType() == Type.PAWN && Math.abs(from.y() - to.y()) == 2) {
            int middleY = (from.y() + to.y()) / 2;
            this.enPassantTarget = Position.of(from.x(), middleY);
            return;
        }
        this.enPassantTarget = null;
    }

    private boolean isPromotion(Position to, Piece piece) {
        if (piece.getType() == Type.PAWN && piece.getColor().getPawnPromotionRank() == to.y()) {
            return true;
        }
        return false;
    }

    private void promotion(Position to, Color color) {
        removePiece(to);
        pieces.put(to, new Queen(color, true));
    }

    public Board movePieceVirtually(Position from, Position to) {
        Board virtualBoard = new Board(this.pieces);

        virtualBoard.movePiece(from, to);

        return virtualBoard;
    }

    private void placePiece(Piece piece, Position position) {
        pieces.put(position, piece);
    }

    private void removePiece(Position position) {
        pieces.remove(position);
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

    public void restore(BoardSnapshot snapshot) {
        this.pieces.clear();
        this.pieces.putAll(snapshot.pieces());
        this.enPassantTarget = snapshot.enPassantTarget();
    }

    public Map<Position, Piece> getPieces() {
        return Collections.unmodifiableMap(pieces);
    }

    public Map<Position, Piece> getPiecesByTeam(Color color) {
        return pieces.entrySet().stream()
                .filter(entry -> entry.getValue().getColor() == color)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Position getEnPassantTarget() {
        return enPassantTarget;
    }
}