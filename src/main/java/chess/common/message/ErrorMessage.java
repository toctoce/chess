package chess.common.message;

public enum ErrorMessage {

    POSITION_INVALID_RANGE("Position 좌표는 0부터 7 사이여야 합니다.: (%s, %s)", true),
    POSITION_IS_EMPTY("대수 기보 표기법이 빈 문자열입니다."),
    POSITION_INVALID_LENGTH("대수 기보 표기법은 두 글자로 구성해야 합니다.: %s", true),
    POSITION_INVALID_FILE("대수 기보 표기법의 첫 번째 글자는 A~H 이어야 합니다.: %s", true),
    POSITION_INVALID_RANK("대수 기보 표기법의 두 번째 글자는 1~8 이어야 합니다.: %s", true),

    PIECE_INVALID_CREATION_ARGUMENTS("Piece 생성 인자는 null일 수 없습니다."),
    PIECE_NOT_FOUND("기물이 존재하지 않습니다.");


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
