package chess.domain.status;

import static org.assertj.core.api.Assertions.assertThat;

import chess.domain.board.Board;
import chess.domain.board.Position;
import chess.domain.game.Game;
import chess.domain.game.GameHistory;
import chess.domain.piece.Color;
import chess.domain.piece.Piece;
import chess.domain.piece.impls.King;
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

class StalemateDetectorTest {

    private final CheckDetector checkDetector = new CheckDetector();
    private final MovementValidator movementValidator = new MovementValidator(checkDetector);
    private final StalemateDetector stalemateDetector = new StalemateDetector(movementValidator, checkDetector);

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideStalemateScenarios")
    @DisplayName("다양한 기물 배치 상황에서 스테일메이트 여부를 정확히 판단한다")
    void isStalemateReturnsCorrectResult(
            String description,
            Board board,
            Color currentTurn,
            boolean expectedResult
    ) {
        Game game = new Game(null, board, currentTurn, GameStatus.ONGOING, new GameHistory());

        boolean actualResult = stalemateDetector.isStalemate(game);

        assertThat(actualResult).isEqualTo(expectedResult);
    }

    static Stream<Arguments> provideStalemateScenarios() {
        return Stream.of(
                Arguments.of(
                        "킹이 구석에 갇히고 공격받지 않지만 이동할 곳이 없음",
                        createBoard(
                                "H8", new King(Color.WHITE),
                                "F7", new King(Color.BLACK),
                                "G6", new Queen(Color.BLACK)
                        ),
                        Color.WHITE,
                        true
                ),
                Arguments.of(
                        "폰과 킹이 모두 막혀 움직일 수 없는 상황",
                        createBoard(
                                "H1", new King(Color.WHITE),
                                "H2", new Pawn(Color.WHITE),
                                "H3", new Pawn(Color.BLACK),
                                "F2", new Queen(Color.BLACK)
                        ),
                        Color.WHITE,
                        true
                ),

                Arguments.of(
                        "체크 상황은 스테일메이트가 아님",
                        createBoard(
                                "A1", new King(Color.WHITE),
                                "A8", new Rook(Color.BLACK),
                                "B1", new Rook(Color.BLACK)
                        ),
                        Color.WHITE,
                        false
                ),
                Arguments.of(
                        "체크메이트 상황은 스테일메이트가 아님",
                        createBoard(
                                "A1", new King(Color.WHITE),
                                "A8", new Rook(Color.BLACK),
                                "B8", new Rook(Color.BLACK)
                        ),
                        Color.WHITE,
                        false
                ),
                Arguments.of(
                        "킹이 갇혔고 다른 기물이 움직일 수 있음",
                        createBoard(
                                "A8", new King(Color.WHITE),
                                "B6", new Queen(Color.BLACK),
                                "A2", new Pawn(Color.WHITE)
                        ),
                        Color.WHITE,
                        false
                ),
                Arguments.of(
                        "킹이 피할 곳이 남아 있음 (일반적인 상황)",
                        createBoard(
                                "E1", new King(Color.WHITE),
                                "E8", new King(Color.BLACK)
                        ),
                        Color.WHITE,
                        false
                ),
                Arguments.of(
                        "아군 기물로 막힌 것이 아니라 잡을 수 있는 적 기물인 경우",
                        createBoard(
                                "A1", new King(Color.WHITE),
                                "B2", new Rook(Color.BLACK)
                        ),
                        Color.WHITE,
                        false
                )
        );
    }

    private static Board createBoard(Object... args) {
        Map<Position, Piece> map = new HashMap<>();
        for (int i = 0; i < args.length; i += 2) {
            String notation = (String) args[i];
            Piece piece = (Piece) args[i + 1];
            map.put(Position.from(notation), piece);
        }
        return new Board(map);
    }
}