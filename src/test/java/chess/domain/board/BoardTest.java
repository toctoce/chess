package chess.domain.board;

import static chess.common.message.ErrorMessage.PIECE_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import chess.common.exception.PieceNotFoundException;
import chess.domain.piece.Color;
import chess.domain.piece.Piece;
import chess.domain.piece.Type;
import chess.domain.piece.impls.Bishop;
import chess.domain.piece.impls.King;
import chess.domain.piece.impls.Knight;
import chess.domain.piece.impls.Pawn;
import chess.domain.piece.impls.Queen;
import chess.domain.piece.impls.Rook;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("Board 객체 테스트")
class BoardTest {

    private Board board;
    private final Position a1 = Position.from("A1");
    private final Position a2 = Position.from("A2");
    private final Position a5 = Position.from("A5");
    private final Position c1 = Position.from("C1");
    private final Position c2 = Position.from("C2");
    private final Position c3 = Position.from("C3");
    private final Position c4 = Position.from("C4");
    private final Position c5 = Position.from("C5");
    private final Position c6 = Position.from("C6");
    private final Position c7 = Position.from("C7");
    private final Position c8 = Position.from("C8");

    @BeforeEach
    void setUp() {
        board = new Board();
        board.initialize();
    }

    static Stream<Arguments> provideInitialPieces() {
        return Stream.of(
                // 흰색 기물 (Rank 1 & 2)
                Arguments.of("A1", Rook.class, Color.WHITE),
                Arguments.of("B1", Knight.class, Color.WHITE),
                Arguments.of("C1", Bishop.class, Color.WHITE),
                Arguments.of("D1", Queen.class, Color.WHITE),
                Arguments.of("E1", King.class, Color.WHITE),
                Arguments.of("F1", Bishop.class, Color.WHITE),
                Arguments.of("G1", Knight.class, Color.WHITE),
                Arguments.of("H1", Rook.class, Color.WHITE),

                // 폰줄 (Rank 2) - 8개 폰
                Arguments.of("A2", Pawn.class, Color.WHITE),
                Arguments.of("B2", Pawn.class, Color.WHITE),
                Arguments.of("C2", Pawn.class, Color.WHITE),
                Arguments.of("D2", Pawn.class, Color.WHITE),
                Arguments.of("E2", Pawn.class, Color.WHITE),
                Arguments.of("F2", Pawn.class, Color.WHITE),
                Arguments.of("G2", Pawn.class, Color.WHITE),
                Arguments.of("H2", Pawn.class, Color.WHITE),

                // 검은색 기물 (Rank 7 & 8)
                Arguments.of("A8", Rook.class, Color.BLACK),
                Arguments.of("B8", Knight.class, Color.BLACK),
                Arguments.of("C8", Bishop.class, Color.BLACK),
                Arguments.of("D8", Queen.class, Color.BLACK),
                Arguments.of("E8", King.class, Color.BLACK),
                Arguments.of("F8", Bishop.class, Color.BLACK),
                Arguments.of("G8", Knight.class, Color.BLACK),
                Arguments.of("H8", Rook.class, Color.BLACK),

                // 폰줄 (Rank 7) - 8개 폰
                Arguments.of("A7", Pawn.class, Color.BLACK),
                Arguments.of("B7", Pawn.class, Color.BLACK),
                Arguments.of("C7", Pawn.class, Color.BLACK),
                Arguments.of("D7", Pawn.class, Color.BLACK),
                Arguments.of("E7", Pawn.class, Color.BLACK),
                Arguments.of("F7", Pawn.class, Color.BLACK),
                Arguments.of("G7", Pawn.class, Color.BLACK),
                Arguments.of("H7", Pawn.class, Color.BLACK)
        );
    }

    @ParameterizedTest
    @MethodSource("provideInitialPieces")
    @DisplayName("Board 초기화 시 32개의 기물이 올바른 위치에 배치된다")
    void boardInitializationPlaces32Pieces(
            String algebraicNotation,
            Class<? extends Piece> expectedType,
            Color expectedColor
    ) {
        Position position = Position.from(algebraicNotation);

        Piece piece = board.getPiece(position);

        assertAll(
                () -> assertThat(piece).isNotNull(),
                () -> assertThat(piece).isInstanceOf(expectedType),
                () -> assertThat(piece.getColor()).isEqualTo(expectedColor)
        );
    }

    @Test
    @DisplayName("Board 초기화 후 중앙 4개 랭크가 모두 비어있다")
    void centralRanksAreEmpty() {
        Board board = new Board();
        board.initialize();

        for (int x = 0; x <= 7; x++) {
            for (int y = 2; y <= 5; y++) {
                Position currentPosition = Position.of(x, y);

                assertThat(board.getPiece(currentPosition)).isNull();
            }
        }
    }

    @Nested
    @DisplayName("movePiece 메서드 테스트")
    class MovePieceTest {

        @Test
        @DisplayName("유효한 이동 시 기물은 출발 위치에서 제거되고 도착 위치에 배치된다")
        void movePieceUpdatesPositions() {
            Piece rook = board.getPiece(a1);

            board.movePiece(a1, a5);

            assertThat(board.getPiece(a5)).isEqualTo(rook);
            assertThat(board.getPiece(a1)).isNull();
            assertThat(board.getPieces()).hasSize(32);
        }

        @Test
        @DisplayName("출발 위치에 기물이 없으면 PieceNotFoundException을 던진다")
        void moveFromEmptyPositionThrowsException() {
            assertThatThrownBy(() -> board.movePiece(c4, a2))
                    .isInstanceOf(PieceNotFoundException.class)
                    .hasMessageContaining(PIECE_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("도착 위치에 기물이 있으면 덮어쓰며 기물을 잡는다 (Map 크기 감소)")
        void movePieceCapturesTarget() {
            board.movePiece(a1, a2);

            assertThat(board.getPieces()).hasSize(31);
            assertThat(board.getPiece(a2)).isInstanceOf(Rook.class);
        }
    }

    @Nested
    @DisplayName("경로 장애물 검사")
    class ObstaclePathTest {

        private Map<Position, Piece> setupPieces;
        private Board boardWithObstacle;

        @BeforeEach
        void setupCustomBoard() {
            setupPieces = new HashMap<>();
        }

        @ParameterizedTest
        @CsvSource(value = {
                "A1, A8, A3",
                "A1, A8, A7",
                "H1, C1, E1",
                "A4, G4, C4",
                "A1, H8, D4",
                "G7, D4, E5",
                "H8, B2, C3"
        })
        @DisplayName("경로에 장애물이 있으면 true를 반환한다")
        void pathIsBlockedReturnsTrue(String fromNotation, String toNotation, String obstacleNotation) {
            Position from = Position.from(fromNotation);
            Position to = Position.from(toNotation);
            Position obstacle = Position.from(obstacleNotation);

            setupPieces.put(obstacle, new Pawn(Color.BLACK));
            boardWithObstacle = new Board(setupPieces);

            assertThat(boardWithObstacle.hasObstacleInPath(from, to)).isTrue();
        }

        @ParameterizedTest
        @CsvSource(value = {
                "A1, A2",
                "A1, A8",
                "A1, B1",
                "B1, B8",
                "A1, B2",
                "D1, H5",
                "D1, D8",
                "H5, D5",
                "H5, C5",
                "H8, D8",
                "H8, D4"
        })
        @DisplayName("경로에 장애물이 없으면 false를 반환한다")
        void pathIsNotBlockedReturnsFalse(String fromNotation, String toNotation) {
            Position from = Position.from(fromNotation);
            Position to = Position.from(toNotation);

            setupPieces.put(c1, new Pawn(Color.BLACK));
            setupPieces.put(c2, new Pawn(Color.BLACK));
            setupPieces.put(c3, new Pawn(Color.BLACK));
            setupPieces.put(c4, new Pawn(Color.BLACK));
            setupPieces.put(c5, new Pawn(Color.BLACK));
            setupPieces.put(c6, new Pawn(Color.BLACK));
            setupPieces.put(c7, new Pawn(Color.BLACK));
            setupPieces.put(c8, new Pawn(Color.BLACK));
            boardWithObstacle = new Board(setupPieces);

            assertThat(boardWithObstacle.hasObstacleInPath(from, to)).isFalse();
        }

        @ParameterizedTest
        @CsvSource(value = {
                "A1, B3",
                "A1, B4",
                "A1, B5",
                "A1, B6",
                "A1, B7",
                "A1, B8",
                "F5, D4",
                "F5, D2",
                "F5, E3",
                "F5, H4"
        })
        @DisplayName("비직선/비대각선 이동은 false를 반환한다")
        void nonLinearMoveReturnsFalse(String fromNotation, String toNotation) {
            Position from = Position.from(fromNotation);
            Position to = Position.from(toNotation);

            Board board = new Board();

            assertThat(board.hasObstacleInPath(from, to)).isFalse();
        }
    }

    @Nested
    @DisplayName("getPieceByTeam 메서드 테스트")
    class GetPieceByTeamTest {
        private Board customBoard;
        private Piece whiteRook;
        private Piece blackPawn;
        private final Position whiteRookPosition = Position.from("A1");
        private final Position blackPqwnPosition = Position.from("H7");

        @BeforeEach
        void setupForAdditionalTests() {
            // 테스트용 독립적인 보드 구성
            Map<Position, Piece> pieces = new HashMap<>();
            whiteRook = new Rook(Color.WHITE);
            blackPawn = new Pawn(Color.BLACK);

            pieces.put(whiteRookPosition, whiteRook);
            pieces.put(blackPqwnPosition, blackPawn);

            customBoard = new Board(pieces);
        }

        @Test
        @DisplayName("백 기물들만 필터링하여 반환한다")
        void getPiecesByTeam_filters_white_pieces() {
            Map<Position, Piece> whitePieces = customBoard.getPiecesByTeam(Color.WHITE);

            assertAll(
                    () -> assertThat(whitePieces).hasSize(1),
                    () -> assertThat(whitePieces).containsEntry(whiteRookPosition, whiteRook),
                    () -> assertThat(whitePieces).doesNotContainKey(blackPqwnPosition)
            );
        }

        @Test
        @DisplayName("흑 기물들만 정확히 필터링하여 반환한다")
        void getPiecesByTeam_filters_black_pieces() {
            Map<Position, Piece> blackPieces = customBoard.getPiecesByTeam(Color.BLACK);

            assertAll(
                    () -> assertThat(blackPieces).hasSize(1),
                    () -> assertThat(blackPieces).containsEntry(blackPqwnPosition, blackPawn),
                    () -> assertThat(blackPieces).doesNotContainKey(whiteRookPosition)
            );
        }

        @Test
        @DisplayName("해당 색상의 기물이 없으면 빈 Map을 반환한다")
        void getPiecesByTeam_returns_empty_if_no_pieces() {
            Board empty = new Board(new HashMap<>());
            assertThat(empty.getPiecesByTeam(Color.WHITE)).isEmpty();
        }
    }

    @Test
    @DisplayName("특정 색상과 종류의 기물 위치를 모두 찾는다")
    void findPositions_returns_all_matching_positions() {
        // given: 초기화된 보드 (A2~H2에 흰색 폰 8개, E1에 흰색 킹 존재)

        // when: 흰색 폰의 위치를 요청
        List<Position> whitePawnPositions = board.findPositions(Color.WHITE, Type.PAWN);

        // then: 8개의 위치가 반환되어야 함
        assertThat(whitePawnPositions).hasSize(8);
        assertThat(whitePawnPositions).contains(Position.from("A2"), Position.from("H2"));
    }

    @Test
    @DisplayName("존재하지 않는 기물을 찾으면 빈 리스트를 반환한다")
    void findPositions_returns_empty_when_not_found() {
        // when: 아직 승진하지 않았으므로 흰색 퀸은 E1(초기위치) 외에 없다고 가정하거나,
        // 더 확실하게 '기물이 없는 보드'를 만들어서 테스트
        Board emptyBoard = new Board(new HashMap<>());

        List<Position> result = emptyBoard.findPositions(Color.WHITE, Type.QUEEN);

        // then
        assertThat(result).isEmpty();
    }
}