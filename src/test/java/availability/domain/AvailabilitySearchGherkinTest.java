package availability.domain;

import org.junit.jupiter.api.Test;
import availability.domain.exception.SlotConflictException;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class AvailabilitySearchGherkinTest {

    private static final Instant START = Instant.parse("2026-11-10T10:00:00Z");

    @Test
    void searchReturnsExactMatchWhenSlotCoversWindow() {
        Availability availability = Availability.forProvider("alice");
        availability.addSlot(BookableSlot.of(Instant.parse("2026-11-10T09:00:00Z"), 4));
        assertTrue(availability.isAvailable(START, 2));
    }

    @Test
    void searchExcludesProviderWithConflictingAvailability() {
        Availability availability = Availability.forProvider("alice");
        availability.addSlot(BookableSlot.of(Instant.parse("2026-11-10T09:00:00Z"), 1));
        assertFalse(availability.isAvailable(Instant.parse("2026-11-10T11:00:00Z"), 2));
    }

    @Test
    void overlappingSlotIsRejected() {
        Availability availability = Availability.forProvider("alice");
        availability.addSlot(BookableSlot.of(START, 2));
        assertThrows(SlotConflictException.class,
                () -> availability.addSlot(BookableSlot.of(START.plusSeconds(30 * 60), 2)));
    }
}
