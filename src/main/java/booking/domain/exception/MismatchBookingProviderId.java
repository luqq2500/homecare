package booking.domain.exception;

public class MismatchBookingProviderId extends RuntimeException {
    public MismatchBookingProviderId(String s) {
        super(s);
    }
}
