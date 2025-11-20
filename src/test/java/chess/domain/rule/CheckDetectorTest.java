package chess.domain.rule;

import static org.assertj.core.api.Assertions.assertThat;

import chess.domain.board.Board;
import chess.domain.board.Position;
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

class CheckDetectorTest {

    private final CheckDetector detector = new CheckDetector();

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideCheckScenarios")
    @DisplayName("다양한 기물 배치 상황에서 체크 여부를 정확히 판단한다")
    void isCheckReturnsCorrectResult(
            String description,
            Board board,
            Color kingColor,
            boolean expectedResult
    ) {
        boolean actualResult = detector.isCheck(board, kingColor);

        assertThat(actualResult).isEqualTo(expectedResult);
    }

    static Stream<Arguments> provideCheckScenarios() {
        return Stream.of(
                Arguments.of(
                        "룩이 장애물 없이 킹을 공격함",
                        createBoard(
                                "E1", new King(Color.WHITE),
                                "E8", new Rook(Color.BLACK)
                        ),
                        Color.WHITE,
                        true
                ),
                Arguments.of(
                        "룩 공격 경로에 아군 기물이 있어 공격 불가",
                        createBoard(
                                "E1", new King(Color.WHITE),
                                "E8", new Rook(Color.BLACK),
                                "E4", new Pawn(Color.WHITE)
                        ),
                        Color.WHITE,
                        false
                ),
                Arguments.of(
                        "나이트는 장애물을 뛰어넘어 킹을 공격함",
                        createBoard(
                                "E1", new King(Color.WHITE),
                                "D3", new Knight(Color.BLACK),
                                "E2", new Pawn(Color.WHITE)
                        ),
                        Color.WHITE,
                        true
                ),
                Arguments.of(
                        "비숍이 대각선으로 킹을 공격함",
                        createBoard(
                                "E1", new King(Color.WHITE),
                                "H4", new Bishop(Color.BLACK)
                        ),
                        Color.WHITE,
                        true
                ),
                Arguments.of(
                        "비숍 공격 경로에 장애물이 있어 공격 불가",
                        createBoard(
                                "E1", new King(Color.WHITE),
                                "H4", new Bishop(Color.BLACK),
                                "F2", new Pawn(Color.WHITE)
                        ),
                        Color.WHITE,
                        false
                ),
                Arguments.of(
                        "퀸이 직선 경로로 킹을 공격함",
                        createBoard(
                                "E1", new King(Color.WHITE),
                                "E8", new Queen(Color.BLACK)
                        ),
                        Color.WHITE,
                        true
                ),
                Arguments.of(
                        "퀸이 대각선 경로로 킹을 공격함",
                        createBoard(
                                "E1", new King(Color.WHITE),
                                "A5", new Queen(Color.BLACK)
                        ),
                        Color.WHITE,
                        true
                ),
                Arguments.of(
                        "검은색 폰이 대각선 위치에서 흰색 킹을 공격함",
                        createBoard(
                                "D4", new King(Color.WHITE),
                                "E5", new Pawn(Color.BLACK)
                        ),
                        Color.WHITE,
                        true
                ),
                Arguments.of(
                        "검은색 폰은 전진 방향으로 킹을 공격할 수 없음",
                        createBoard(
                                "D4", new King(Color.WHITE),
                                "D5", new Pawn(Color.BLACK)
                        ),
                        Color.WHITE,
                        false
                ),
                Arguments.of(
                        "흰색 기물이 검은색 킹을 공격함",
                        createBoard(
                                "E8", new King(Color.BLACK),
                                "E1", new Rook(Color.WHITE)
                        ),
                        Color.BLACK,
                        true
                ),
                Arguments.of(
                        "같은 편 기물끼리는 공격 판정이 되지 않음",
                        createBoard(
                                "E1", new King(Color.WHITE),
                                "E8", new Rook(Color.WHITE)
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