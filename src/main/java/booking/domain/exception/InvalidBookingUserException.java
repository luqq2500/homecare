package booking.domain.exception;

public class InvalidBookingUserException extends RuntimeException {
    public InvalidBookingUserException(String s) {
        super(s);
    }
}
