package chess.repository;

import chess.domain.game.Game;
import java.util.Optional;

public interface GameRepository {
    Game save(Game game);

    Optional<Game> findById(Long id);

    void deleteById(Long id);
}