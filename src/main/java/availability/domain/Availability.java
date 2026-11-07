package availability.domain;

import availability.domain.exception.SlotConflictException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Aggregate root for a provider's availability.
 * Owns bookable slots and basic conflict detection.
 */
public class Availability {

    private final String providerId;
    private final List<BookableSlot> slots = new ArrayList<>();

    private Availability(String providerId) {
        this.providerId = Objects.requireNonNull(providerId);
    }

    public static Availability forProvider(String providerId) {
        return new Availability(providerId);
    }

    public void addSlot(BookableSlot slot) {
        Objects.requireNonNull(slot);
        for (BookableSlot existing : slots) {
            if (existing.overlaps(slot)) {
                throw new SlotConflictException("Slot overlaps an existing bookable window");
            }
        }
        this.slots.add(slot);
    }

    public void removeSlot(Instant start) {
        slots.removeIf(s -> s.start().equals(start));
    }

    public boolean isAvailable(Instant start, int durationHours) {
        Instant end = start.plusSeconds(durationHours * 3600L);
        return slots.stream().anyMatch(s ->
                !s.start().isAfter(start) && !s.end().isBefore(end));
    }

    public String providerId() { return providerId; }
    public List<BookableSlot> slots() { return Collections.unmodifiableList(slots); }
}
