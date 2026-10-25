package booking.domain.exception;

public class InvalidBookingProviderException extends RuntimeException {
    public InvalidBookingProviderException(String s) {
        super(s);
    }
}
