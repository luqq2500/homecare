package booking.domain.exception;

public class InvalidBookingException extends RuntimeException {
    public InvalidBookingException(String s) {
        super(s);
    }
}
