package booking.domain.exception;

public class UserNotEligibleToAcceptBookingConfirmationException extends RuntimeException {
    public UserNotEligibleToAcceptBookingConfirmationException(String s) {
        super(s);
    }
}
