package chess.domain.status;

public enum GameStatus {
    ONGOING("진행 중"),

    CHECKMATE_WHITE_WIN("흰색 플레이어 승리 (체크메이트)"),
    CHECKMATE_BLACK_WIN("검은색 플레이어 승리 (체크메이트)"),
    RESIGNATION_WHITE_WIN("흰색 플레이어 승리 (상대 기권)"),
    RESIGNATION_BLACK_WIN("검은색 플레이어 승리 (상대 기권)"),

    STALEMATE_DRAW("무승부 (스테일메이트)"),
    INSUFFICIENT_MATERIAL_DRAW("무승부 (기물 부족)"),
    FIFTY_MOVE_RULE_DRAW("무승부 (50수 규칙)"),
    REPETITION_DRAW("무승부 (같은 상태 3회 반복)"),
    AGREEMENT_DRAW("무승부 (합의)");

    private final String description;

    GameStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isFinished() {
        return this != ONGOING;
    }
}