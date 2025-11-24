package chess.repository.impls;

import chess.common.exception.RepositoryException;
import chess.common.message.ErrorMessage;
import chess.domain.game.Game;
import chess.repository.GameRepository;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryGameRepository implements GameRepository {

    private final Map<Long, Game> storage = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(1L);

    @Override
    public Game save(Game game) {
        if (game.getId() == null) {
            long id = sequence.getAndIncrement();
            injectId(game, id);
            storage.put(id, game);
            return game;
        }
        storage.put(game.getId(), game);
        return game;
    }

    @Override
    public Optional<Game> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public void deleteById(Long id) {
        storage.remove(id);
    }

    private void injectId(Game game, long id) {
        try {
            Field idField = Game.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(game, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RepositoryException(ErrorMessage.REPOSITORY_ID_INJECTION_FAILED.getMessage());
        }
    }
}