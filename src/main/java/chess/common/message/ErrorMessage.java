package chess.common.message;

public enum ErrorMessage {

    POSITION_INVALID_RANGE("Position 좌표는 0부터 7 사이여야 합니다.: (%s, %s)", true),
    POSITION_IS_EMPTY("대수 기보 표기법이 빈 문자열입니다."),
    POSITION_INVALID_LENGTH("대수 기보 표기법은 두 글자로 구성해야 합니다.: %s", true),
    POSITION_INVALID_FILE("대수 기보 표기법의 첫 번째 글자는 A~H 이어야 합니다.: %s", true),
    POSITION_INVALID_RANK("대수 기보 표기법의 두 번째 글자는 1~8 이어야 합니다.: %s", true),

    PIECE_INVALID_CREATION_ARGUMENTS("Piece 생성 인자는 null일 수 없습니다."),
    PIECE_NOT_FOUND("기물이 존재하지 않습니다."),

    RULE_WRONG_TURN_PIECE("자신의 턴에 맞는 기물만 움직일 수 있습니다."),
    RULE_SAME_POSITION_MOVE("제자리로 이동할 수 없습니다."),
    RULE_FRIENDLY_FIRE("아군 기물이 있는 위치로 이동할 수 없습니다."),
    RULE_INVALID_PIECE_MOVE("해당 기물의 이동 규칙에 어긋납니다."),
    RULE_PATH_BLOCKED("이동 경로에 장애물이 있어 이동할 수 없습니다."),

    RULE_KING_IN_CHECK_AFTER_MOVE("킹이 체크 상태가 되는 위치로는 이동할 수 없습니다."),

    GAME_ALREADY_FINISHED("게임이 이미 종료되었습니다: %s", true),
    STATE_INVALID_WINNER_COLOR("승리자의 색상이 WHITE 또는 BLACK이 아닙니다."),

    NO_HISTORY("기록이 없습니다.");

    private final String message;
    private final boolean isFormatted;

    private ErrorMessage(String message) {
        this.message = message;
        this.isFormatted = false;
    }

    private ErrorMessage(String message, boolean isFormatted) {
        this.message = message;
        this.isFormatted = isFormatted;
    }

    public String getMessage() {
        return this.message;
    }

    public String getMessage(String... arguments) {
        return this.isFormatted ? String.format(this.message, arguments) : this.message;
    }
}
