package booking.domain;

public class InvalidCancelBooking extends RuntimeException {
    public InvalidCancelBooking(String s) {
        super(s);
    }
}
