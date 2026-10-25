package booking.domain.exception;

public class UserNotEligibleToRequestBookingException extends RuntimeException {
    public UserNotEligibleToRequestBookingException(String s) {
        super(s);
    }
}
