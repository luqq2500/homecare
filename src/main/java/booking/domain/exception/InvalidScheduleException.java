package booking.domain.exception;

public class InvalidScheduleException extends RuntimeException {
    public InvalidScheduleException(String s) {
        super(s);
    }
}
