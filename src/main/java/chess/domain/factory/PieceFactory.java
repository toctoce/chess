package chess.domain.factory;

import chess.domain.piece.Piece;

public interface PieceFactory {

    Piece createKing();

    Piece createQueen();

    Piece createRook();

    Piece createBishop();

    Piece createKnight();

    Piece createPawn();
}