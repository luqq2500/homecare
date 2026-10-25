package booking.domain.exception;

public class UserNotEligibleToCancelBookingException extends RuntimeException {
    public UserNotEligibleToCancelBookingException(String s) {
        super(s);
    }
}
