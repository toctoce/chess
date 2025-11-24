package chess.controller;

import chess.dto.ChessGameResponseDto;
import chess.dto.MoveRequestDto;
import chess.service.GameService;
import jakarta.servlet.http.HttpSession;
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
    public ResponseEntity<ChessGameResponseDto> startGame(HttpSession session) {
        ChessGameResponseDto response = gameService.startGame(session.getId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<ChessGameResponseDto> joinGame(@PathVariable Long id, HttpSession session) {
        ChessGameResponseDto response = gameService.joinGame(id, session.getId());
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
            @RequestBody MoveRequestDto moveRequest,
            HttpSession session
    ) {
        ChessGameResponseDto response = gameService.move(id, moveRequest, session.getId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/undo")
    public ResponseEntity<ChessGameResponseDto> undo(@PathVariable Long id) {
        ChessGameResponseDto response = gameService.undo(id);
        return ResponseEntity.ok(response);
    }
}