package booking.domain.event;

import java.time.LocalDateTime;
import java.util.List;

public class BookingEdited implements DomainEvent {
    private final String userId;
    private final List<String> newOfferingIds;
    private final LocalDateTime newStart;
    private final LocalDateTime newEnd;
    private final LocalDateTime occurredOn;

    public BookingEdited(String userId, List<String> newOfferingIds, LocalDateTime newStart, LocalDateTime newEnd) {
        this.userId = userId;
        this.newOfferingIds = newOfferingIds;
        this.newStart = newStart;
        this.newEnd = newEnd;
        this.occurredOn = LocalDateTime.now();
    }

    @Override
    public LocalDateTime occurredOn() {
        return this.occurredOn;
    }
}
