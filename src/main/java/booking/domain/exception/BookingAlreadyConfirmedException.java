package booking.domain.exception;

public class BookingAlreadyConfirmedException extends RuntimeException {
    public BookingAlreadyConfirmedException(String s) {
        super(s);
    }
}
