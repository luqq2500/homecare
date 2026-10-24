package booking.domain;

import java.time.LocalDateTime;

public record Schedule(LocalDateTime start, LocalDateTime end) {
    public void validate() throws RuntimeException {
        if (!start().isBefore(end)){
             throw new InvalidBookingSchedule("Booking cannot start after schedule ends.");
        }
    }
}
