package booking.domain.exception;

public class ConfirmationNotBelongsToBookingException extends RuntimeException {
    public ConfirmationNotBelongsToBookingException(String s, String bookingId) {
        super(s);
    }
}
