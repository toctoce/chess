package chess.common.message;

public enum ErrorMessage {

    INVALID_POSITION_RANGE("Position 좌표는 0부터 7 사이여야 합니다.: (%s, %s)", true);


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
