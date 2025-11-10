package chess.domain.board;

public record Position(int x, int y) {

    public Position {
        validate(x, y);
    }

    public static Position of(int x, int y) {
        return new Position(x, y);
    }

    /** "A1"같은 대수 기보 표기법(algebraicNotation)을 이용해 Position 생성 */
    public static Position from(String algebraicNotation) {
        validateAlgebraicNotation(algebraicNotation);

        int x = algebraicNotation.charAt(0) - 'A';
        int y = algebraicNotation.charAt(1) - '1';

        return new Position(x, y);
    }

    private static void validate(int x, int y) {
        if (x < 0 || x > 7 || y < 0 || y > 7) {
            throw new IllegalArgumentException(String.format("Position 좌표는 0부터 7 사이여야 합니다.: (%d, %d)", x, y));
        }
    }

    private static void validateAlgebraicNotation(String algebraicNotation) {
        if (algebraicNotation == null || algebraicNotation.isBlank()) {
            throw new IllegalArgumentException("대수 기보 표기법이 빈 문자열입니다.");
        }

        if (algebraicNotation.length() != 2) {
            throw new IllegalArgumentException("대수 기보 표기법은 두 글자로 구성해야 합니다.: " + algebraicNotation);
        }

        char firstLetter = algebraicNotation.charAt(0);
        char secondLetter = algebraicNotation.charAt(1);

        if (firstLetter < 'A' || firstLetter > 'H') {
            throw new IllegalArgumentException("대수 기보 표기법의 첫 번째 글자는 A~H 이어야 합니다.: " + algebraicNotation);
        }
        if (secondLetter < '1' || secondLetter > '8') {
            throw new IllegalArgumentException("대수 기보 표기법의 두 번째 글자는 1~8 이어야 합니다.: " + algebraicNotation);
        }
    }
}