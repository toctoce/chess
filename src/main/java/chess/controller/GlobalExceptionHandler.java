package chess.controller;

import chess.common.exception.ChessException;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ChessException.class)
    public ResponseEntity<Map<String, String>> handleChessException(ChessException e) {
        // main.js가 response.json()으로 파싱 후 .message를 읽으므로 JSON 형태로 반환
        return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
    }

    // 기타 런타임 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception e) {
        e.printStackTrace(); // 로깅용
        return ResponseEntity.internalServerError().body(Map.of("message", "서버 내부 오류가 발생했습니다."));
    }
}