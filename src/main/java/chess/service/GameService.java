package chess.service;

import chess.common.exception.GameNotFoundException;
import chess.common.message.ErrorMessage;
import chess.domain.board.Board;
import chess.domain.board.Position;
import chess.domain.game.Game;
import chess.domain.status.MovementValidator;
import chess.domain.status.StatusCalculator;
import chess.dto.ChessGameResponseDto;
import chess.dto.MoveRequestDto;
import chess.repository.GameRepository;
import org.springframework.stereotype.Service;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final MovementValidator movementValidator;
    private final StatusCalculator statusCalculator;

    public GameService(GameRepository gameRepository,
                       MovementValidator movementValidator,
                       StatusCalculator statusCalculator) {
        this.gameRepository = gameRepository;
        this.movementValidator = movementValidator;
        this.statusCalculator = statusCalculator;
    }

    public ChessGameResponseDto startGame() {
        Board board = new Board();
        board.initialize();

        Game game = new Game(board);
        Game savedGame = gameRepository.save(game);

        return ChessGameResponseDto.from(savedGame);
    }

    public ChessGameResponseDto load(Long gameId) {
        Game game = findGameById(gameId);
        return ChessGameResponseDto.from(game);
    }

    public ChessGameResponseDto move(Long gameId, MoveRequestDto moveRequest) {
        Game game = findGameById(gameId);

        Position from = Position.from(moveRequest.from());
        Position to = Position.from(moveRequest.to());

        game.move(from, to, movementValidator, statusCalculator);

        gameRepository.save(game);
        return ChessGameResponseDto.from(game);
    }

    public ChessGameResponseDto undo(Long gameId) {
        Game game = findGameById(gameId);
        game.undo();

        gameRepository.save(game);
        return ChessGameResponseDto.from(game);
    }

    private Game findGameById(Long gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException(ErrorMessage.GAME_NOT_FOUND.getMessage(
                        String.valueOf(gameId))));
    }
}