package booking.domain;

public class InvalidBookingSchedule extends RuntimeException {
    public InvalidBookingSchedule(String s) {
        super(s);
    }
}
