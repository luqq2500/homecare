package booking.domain;

public class InvalidCreateBookingRequest extends RuntimeException {
    public InvalidCreateBookingRequest(String s) {
        super(s);
    }
}
