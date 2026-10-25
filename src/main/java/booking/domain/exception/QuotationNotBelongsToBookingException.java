package booking.domain.exception;

public class QuotationNotBelongsToBookingException extends RuntimeException {
    public QuotationNotBelongsToBookingException(String s, String bookingId) {
        super(s);
    }
}
