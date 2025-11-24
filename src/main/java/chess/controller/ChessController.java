package chess.controller;

import chess.dto.ChessGameResponseDto;
import chess.dto.MoveRequestDto;
import chess.service.GameService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/games")
public class ChessController {

    private final GameService gameService;

    public ChessController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping
    public ResponseEntity<ChessGameResponseDto> startGame() {
        ChessGameResponseDto response = gameService.startGame();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChessGameResponseDto> loadGame(@PathVariable Long id) {
        ChessGameResponseDto response = gameService.load(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/move")
    public ResponseEntity<ChessGameResponseDto> move(
            @PathVariable Long id,
            @RequestBody MoveRequestDto moveRequest
    ) {
        ChessGameResponseDto response = gameService.move(id, moveRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/undo")
    public ResponseEntity<ChessGameResponseDto> undo(@PathVariable Long id) {
        ChessGameResponseDto response = gameService.undo(id);
        return ResponseEntity.ok(response);
    }
}