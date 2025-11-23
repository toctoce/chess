package chess.domain.status;

import static org.assertj.core.api.Assertions.assertThat;

import chess.domain.board.Board;
import chess.domain.board.Position;
import chess.domain.game.Game;
import chess.domain.piece.Color;
import chess.domain.piece.Piece;
import chess.domain.piece.impls.Bishop;
import chess.domain.piece.impls.King;
import chess.domain.piece.impls.Knight;
import chess.domain.piece.impls.Pawn;
import chess.domain.piece.impls.Queen;
import chess.domain.piece.impls.Rook;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class InsufficientMaterialDetectorTest {

    private final InsufficientMaterialDetector detector = new InsufficientMaterialDetector();

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideInsufficientMaterialScenarios")
    @DisplayName("기물 부족(무승부) 상황인지 정확히 판별한다")
    void isInsufficientMaterialReturnsCorrectResult(
            String description,
            Map<Position, Piece> pieceMap,
            boolean expectedResult
    ) {
        // given
        Board board = new Board(pieceMap);
        Game game = new Game(board); // Game 객체 생성

        // when
        boolean actualResult = detector.isInsufficientMaterial(game);

        // then
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    static Stream<Arguments> provideInsufficientMaterialScenarios() {
        return Stream.of(
                // -------------------------------------------------------
                // 1. 기물 부족으로 인한 무승부 (True Cases)
                // -------------------------------------------------------
                Arguments.of(
                        "킹 vs 킹",
                        createMap(
                                "A1", new King(Color.WHITE),
                                "H8", new King(Color.BLACK)
                        ),
                        true
                ),
                Arguments.of(
                        "킹 vs 킹 + 나이트",
                        createMap(
                                "A1", new King(Color.WHITE),
                                "H8", new King(Color.BLACK),
                                "H7", new Knight(Color.BLACK)
                        ),
                        true
                ),
                Arguments.of(
                        "킹 vs 킹 + 비숍",
                        createMap(
                                "A1", new King(Color.WHITE),
                                "H8", new King(Color.BLACK),
                                "H7", new Bishop(Color.BLACK)
                        ),
                        true
                ),
                Arguments.of(
                        "킹 + 나이트 vs 킹 + 나이트",
                        createMap(
                                "A1", new King(Color.WHITE),
                                "B1", new Knight(Color.WHITE),
                                "H8", new King(Color.BLACK),
                                "G8", new Knight(Color.BLACK)
                        ),
                        true
                ),
                Arguments.of(
                        "킹 + 비숍 vs 킹 + 비숍",
                        createMap(
                                "A1", new King(Color.WHITE),
                                "B1", new Bishop(Color.WHITE),
                                "H8", new King(Color.BLACK),
                                "G8", new Bishop(Color.BLACK)
                        ),
                        true
                ),
                Arguments.of(
                        "킹 + 나이트 vs 킹 + 비숍",
                        createMap(
                                "A1", new King(Color.WHITE),
                                "B1", new Knight(Color.WHITE),
                                "H8", new King(Color.BLACK),
                                "G8", new Bishop(Color.BLACK)
                        ),
                        true
                ),
                Arguments.of(
                        "킹 vs 킹 + 나이트 2개",
                        createMap(
                                "A1", new King(Color.WHITE),
                                "H8", new King(Color.BLACK),
                                "G8", new Knight(Color.BLACK),
                                "F8", new Knight(Color.BLACK)
                        ),
                        true
                ),

                Arguments.of(
                        "폰이 있으면 무승부가 아님",
                        createMap(
                                "A1", new King(Color.WHITE),
                                "H8", new King(Color.BLACK),
                                "H7", new Pawn(Color.BLACK)
                        ),
                        false
                ),
                Arguments.of(
                        "룩이 있으면 무승부가 아님",
                        createMap(
                                "A1", new King(Color.WHITE),
                                "H8", new King(Color.BLACK),
                                "H7", new Rook(Color.BLACK)
                        ),
                        false
                ),
                Arguments.of(
                        "퀸이 있으면 무승부가 아님",
                        createMap(
                                "A1", new King(Color.WHITE),
                                "H8", new King(Color.BLACK),
                                "H7", new Queen(Color.BLACK)
                        ),
                        false
                ),
                Arguments.of(
                        "킹 + 룩 vs 킹 + 나이트",
                        createMap(
                                "A1", new King(Color.WHITE),
                                "B1", new Rook(Color.WHITE),
                                "H8", new King(Color.BLACK),
                                "G8", new Knight(Color.BLACK)
                        ),
                        false
                ),
                Arguments.of(
                        "킹 + 나이트 + 나이트 vs 킹 + 폰",
                        createMap(
                                "A1", new King(Color.WHITE),
                                "B1", new Knight(Color.WHITE),
                                "C1", new Knight(Color.WHITE),
                                "H8", new King(Color.BLACK),
                                "H7", new Pawn(Color.BLACK)
                        ),
                        false
                )
        );
    }

    private static Map<Position, Piece> createMap(Object... args) {
        Map<Position, Piece> map = new HashMap<>();
        for (int i = 0; i < args.length; i += 2) {
            String notation = (String) args[i];
            Piece piece = (Piece) args[i + 1];
            map.put(Position.from(notation), piece);
        }
        return map;
    }
}