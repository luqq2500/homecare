package booking.domain.event;

import java.time.LocalDateTime;

public class BookingCancelled implements DomainEvent {
    private final String userId;
    private final LocalDateTime occurredOn;

    public BookingCancelled(String userId) {
        this.userId = userId;
        this.occurredOn = LocalDateTime.now();
    }

    @Override
    public LocalDateTime occurredOn() {
        return this.occurredOn;
    }
}
