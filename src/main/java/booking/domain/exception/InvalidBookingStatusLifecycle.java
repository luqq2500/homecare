package booking.domain.exception;

public class InvalidBookingStatusLifecycle extends RuntimeException {
    public InvalidBookingStatusLifecycle(String s) {
        super(s);
    }
}
