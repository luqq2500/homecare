package booking.domain.exception;

public class InvalidBookingSchedule extends RuntimeException {
    public InvalidBookingSchedule(String s) {
        super(s);
    }
}
