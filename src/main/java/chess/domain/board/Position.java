package chess.domain.board;

import static chess.common.message.ErrorMessage.POSITION_INVALID_FILE;
import static chess.common.message.ErrorMessage.POSITION_INVALID_LENGTH;
import static chess.common.message.ErrorMessage.POSITION_INVALID_RANGE;
import static chess.common.message.ErrorMessage.POSITION_INVALID_RANK;
import static chess.common.message.ErrorMessage.POSITION_IS_EMPTY;

import chess.common.exception.InvalidPositionException;

public record Position(int x, int y) {

    public Position {
        validate(x, y);
    }

    public static Position of(int x, int y) {
        return new Position(x, y);
    }

    /**
     * "A1"같은 대수 기보 표기법(algebraicNotation)을 이용해 Position 생성
     */
    public static Position from(String algebraicNotation) {
        validateAlgebraicNotation(algebraicNotation);

        int x = algebraicNotation.charAt(0) - 'A';
        int y = algebraicNotation.charAt(1) - '1';

        return new Position(x, y);
    }

    private static void validate(int x, int y) {
        if (x < 0 || x > 7 || y < 0 || y > 7) {
            throw new InvalidPositionException(POSITION_INVALID_RANGE.getMessage(String.valueOf(x), String.valueOf(y)));
        }
    }

    private static void validateAlgebraicNotation(String algebraicNotation) {
        if (algebraicNotation == null || algebraicNotation.isBlank()) {
            throw new InvalidPositionException(POSITION_IS_EMPTY.getMessage());
        }

        if (algebraicNotation.length() != 2) {
            throw new InvalidPositionException(POSITION_INVALID_LENGTH.getMessage(algebraicNotation));
        }

        char firstLetter = algebraicNotation.charAt(0);
        char secondLetter = algebraicNotation.charAt(1);

        if (firstLetter < 'A' || firstLetter > 'H') {
            throw new InvalidPositionException(POSITION_INVALID_FILE.getMessage(algebraicNotation));
        }
        if (secondLetter < '1' || secondLetter > '8') {
            throw new InvalidPositionException(POSITION_INVALID_RANK.getMessage(algebraicNotation));
        }
    }
}