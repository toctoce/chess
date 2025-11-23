package chess.domain.status;

import static org.assertj.core.api.Assertions.assertThat;

import chess.domain.board.Board;
import chess.domain.board.Position;
import chess.domain.piece.Color;
import chess.domain.piece.Piece;
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

class CheckmateDetectorTest {

    private final CheckDetector checkDetector = new CheckDetector();
    private final MovementValidator movementValidator = new MovementValidator(checkDetector);
    private final CheckmateDetector checkmateDetector = new CheckmateDetector(checkDetector, movementValidator);

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideCheckmateScenarios")
    @DisplayName("다양한 기물 배치 상황에서 체크메이트 여부를 정확히 판단한다")
    void isCheckmateReturnsCorrectResult(
            String description,
            Board board,
            Color currentTurn,
            boolean expectedResult
    ) {
        boolean actualResult = checkmateDetector.isCheckmate(board, currentTurn);

        assertThat(actualResult).isEqualTo(expectedResult);
    }

    static Stream<Arguments> provideCheckmateScenarios() {
        return Stream.of(
                Arguments.of(
                        "킹이 공격받고 피할 곳이 없음",
                        createBoard(
                                "A1", new King(Color.WHITE),
                                "A8", new Rook(Color.BLACK),
                                "B1", new Pawn(Color.WHITE),
                                "B2", new Pawn(Color.WHITE)
                        ),
                        Color.WHITE,
                        true
                ),
                Arguments.of(
                        "협공을 받아 피할 곳이 없음",
                        createBoard(
                                "H1", new King(Color.WHITE),
                                "H8", new Rook(Color.BLACK),
                                "A1", new Rook(Color.BLACK),
                                "E3", new Knight(Color.BLACK)
                        ),
                        Color.WHITE,
                        true
                ),
                Arguments.of(
                        "체크가 아닌 상황은 체크메이트가 아님",
                        createBoard(
                                "A1", new King(Color.WHITE),
                                "H8", new Rook(Color.BLACK)
                        ),
                        Color.WHITE,
                        false
                ),
                Arguments.of(
                        "스테일메이트 상황은 체크메이트가 아님",
                        createBoard(
                                "H8", new King(Color.WHITE),
                                "F7", new King(Color.BLACK),
                                "G6", new Queen(Color.BLACK)
                        ),
                        Color.WHITE,
                        false
                ),
                Arguments.of(
                        "킹이 피할 곳이 남아 있음",
                        createBoard(
                                "E1", new King(Color.WHITE),
                                "E8", new Rook(Color.BLACK)
                        ),
                        Color.WHITE,
                        false
                ),
                Arguments.of(
                        "공격 경로를 아군 기물로 막을 수 있음",
                        createBoard(
                                "E1", new King(Color.WHITE),
                                "E8", new Rook(Color.BLACK),
                                "D5", new Rook(Color.WHITE)
                        ),
                        Color.WHITE,
                        false
                ),
                Arguments.of(
                        "공격하는 기물을 킹이 잡을 수 있음",
                        createBoard(
                                "A1", new King(Color.WHITE),
                                "A2", new Rook(Color.BLACK)
                        ),
                        Color.WHITE,
                        false
                ),
                Arguments.of(
                        "공격하는 기물을 다른 기물로 잡을 수 있음",
                        createBoard(
                                "A1", new King(Color.WHITE),
                                "A3", new Rook(Color.BLACK),
                                "B3", new Rook(Color.WHITE)
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