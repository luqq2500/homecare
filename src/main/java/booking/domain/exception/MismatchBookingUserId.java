package booking.domain.exception;

public class MismatchBookingUserId extends RuntimeException {
    public MismatchBookingUserId(String s) {
        super(s);
    }
}
