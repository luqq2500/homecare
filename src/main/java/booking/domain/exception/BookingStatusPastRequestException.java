package booking.domain.exception;

public class BookingStatusPastRequestException extends RuntimeException {
    public BookingStatusPastRequestException(String s) {
        super(s);
    }
}
