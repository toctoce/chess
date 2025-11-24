package chess.dto;

import chess.domain.board.Board;
import chess.domain.board.Position;
import chess.domain.game.Game;
import chess.domain.piece.Piece;
import java.util.HashMap;
import java.util.Map;

public record ChessGameResponseDto(Long gameId, String currentTurn, String status, Map<String, String> board) {

    public static ChessGameResponseDto from(Game game) {
        Map<String, String> board = convertBoardToMap(game.getBoard());
        return new ChessGameResponseDto(
                game.getId(),
                game.getCurrentTurn().name(),
                game.getStatus().name(),
                board
        );
    }

    private static Map<String, String> convertBoardToMap(Board board) {
        Map<String, String> boardMap = new HashMap<>();
        Map<Position, Piece> pieces = board.getPieces();

        pieces.forEach((position, piece) -> {
            String key = position.toAlgebraicNotation();
            String value = piece.getSymbol();
            boardMap.put(key, value);
        });

        return boardMap;
    }
}