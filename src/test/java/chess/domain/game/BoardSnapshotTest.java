package chess.domain.game;

import static org.assertj.core.api.Assertions.assertThat;

import chess.domain.board.Position;
import chess.domain.piece.Color;
import chess.domain.piece.Piece;
import chess.domain.piece.impls.Rook;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BoardSnapshotTest {

    @Test
    @DisplayName("스냅샷 생성 시 원본 Map을 방어적 복사하여 불변성을 유지한다")
    void snapshotImmutability() {
        Map<Position, Piece> originalPieces = new HashMap<>();
        Position a1 = Position.from("A1");
        originalPieces.put(a1, new Rook(Color.WHITE));

        BoardSnapshot snapshot = new BoardSnapshot(originalPieces, Color.WHITE);

        originalPieces.clear();

        assertThat(snapshot.pieces()).hasSize(1);
        assertThat(snapshot.pieces()).containsKey(a1);
    }

    @Test
    @DisplayName("내용이 같은 스냅샷은 동등하다 (Map Key 사용 가능 여부 확인)")
    void snapshotEquality() {
        Map<Position, Piece> map1 = new HashMap<>();
        map1.put(Position.from("A1"), new Rook(Color.WHITE));

        Map<Position, Piece> map2 = new HashMap<>();
        map2.put(Position.from("A1"), new Rook(Color.WHITE));

        BoardSnapshot snapshot1 = new BoardSnapshot(map1, Color.WHITE);
        BoardSnapshot snapshot2 = new BoardSnapshot(map2, Color.WHITE);

        assertThat(snapshot1).isEqualTo(snapshot2);
        assertThat(snapshot1.hashCode()).isEqualTo(snapshot2.hashCode());
    }
}