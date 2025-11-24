package chess.config;

import chess.domain.status.CheckDetector;
import chess.domain.status.CheckmateDetector;
import chess.domain.status.FiftyMoveDetector;
import chess.domain.status.InsufficientMaterialDetector;
import chess.domain.status.MovementValidator;
import chess.domain.status.RepetitionDetector;
import chess.domain.status.StalemateDetector;
import chess.domain.status.StatusCalculator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChessConfig {

    @Bean
    public CheckDetector checkDetector() {
        return new CheckDetector();
    }

    @Bean
    public MovementValidator movementValidator(CheckDetector checkDetector) {
        return new MovementValidator(checkDetector);
    }

    @Bean
    public CheckmateDetector checkmateDetector(CheckDetector checkDetector, MovementValidator movementValidator) {
        return new CheckmateDetector(checkDetector, movementValidator);
    }

    @Bean
    public StalemateDetector stalemateDetector(MovementValidator movementValidator, CheckDetector checkDetector) {
        return new StalemateDetector(movementValidator, checkDetector);
    }

    @Bean
    public FiftyMoveDetector fiftyMoveDetector() {
        return new FiftyMoveDetector();
    }

    @Bean
    public RepetitionDetector repetitionDetector() {
        return new RepetitionDetector();
    }

    @Bean
    public InsufficientMaterialDetector insufficientMaterialDetector() {
        return new InsufficientMaterialDetector();
    }

    @Bean
    public StatusCalculator statusCalculator(
            CheckmateDetector checkmateDetector,
            StalemateDetector stalemateDetector,
            FiftyMoveDetector fiftyMoveDetector,
            RepetitionDetector repetitionDetector,
            InsufficientMaterialDetector insufficientMaterialDetector
    ) {
        return new StatusCalculator(
                checkmateDetector,
                stalemateDetector,
                fiftyMoveDetector,
                repetitionDetector,
                insufficientMaterialDetector
        );
    }
}