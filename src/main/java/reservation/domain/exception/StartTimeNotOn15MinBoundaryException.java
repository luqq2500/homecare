package reservation.domain.exception;

public class StartTimeNotOn15MinBoundaryException extends RuntimeException {
    public StartTimeNotOn15MinBoundaryException(String message) {
        super(message);
    }
}
