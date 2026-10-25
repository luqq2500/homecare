package booking.domain.event;

import java.time.LocalDateTime;
import java.util.List;

public class BookingRequested implements DomainEvent {
    private final String userId;
    private final String providerId;
    private final String categoryId;
    private final List<String> offeringIds;
    private final LocalDateTime start;
    private final LocalDateTime end;

    public BookingRequested(String userId, String providerId, String categoryId, List<String> offeringIds, LocalDateTime start, LocalDateTime end) {
        this.userId = userId;
        this.providerId = providerId;
        this.categoryId = categoryId;
        this.offeringIds = offeringIds;
        this.start = start;
        this.end = end;
    }

    @Override
    public LocalDateTime occurredOn() {
        return null;
    }
}
