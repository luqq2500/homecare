package booking.domain;

public class InvalidBookingStatusLifecycle extends RuntimeException {
    public InvalidBookingStatusLifecycle(String s) {
        super(s);
    }
}
