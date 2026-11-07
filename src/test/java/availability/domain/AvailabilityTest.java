package availability.domain;

import org.junit.jupiter.api.Test;
import availability.domain.exception.SlotConflictException;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class AvailabilityTest {

    private static final Instant START = Instant.parse("2026-11-08T14:00:00Z");

    @Test
    void forProviderCreatesEmptyAvailability() {
        Availability availability = Availability.forProvider("prov-1");
        assertEquals("prov-1", availability.providerId());
        assertTrue(availability.slots().isEmpty());
    }

    @Test
    void addSlotSucceedsWhenNoOverlap() {
        Availability availability = Availability.forProvider("prov-1");
        availability.addSlot(BookableSlot.of(START, 2));
        assertEquals(1, availability.slots().size());
    }

    @Test
    void addOverlappingSlotIsRejected() {
        Availability availability = Availability.forProvider("prov-1");
        availability.addSlot(BookableSlot.of(START, 2));
        assertThrows(SlotConflictException.class,
                () -> availability.addSlot(BookableSlot.of(START.plusSeconds(30 * 60), 2)));
    }

    @Test
    void isAvailableReturnsTrueWhenSlotCoversWindow() {
        Availability availability = Availability.forProvider("prov-1");
        availability.addSlot(BookableSlot.of(START, 4));
        assertTrue(availability.isAvailable(START, 2));
        assertTrue(availability.isAvailable(START.plusSeconds(3600), 2));
    }

    @Test
    void isAvailableReturnsFalseWhenNoCoveringSlot() {
        Availability availability = Availability.forProvider("prov-1");
        availability.addSlot(BookableSlot.of(START, 2));
        assertFalse(availability.isAvailable(START.plusSeconds(3 * 3600), 2));
    }

    @Test
    void removeSlotDeletesMatchingStart() {
        Availability availability = Availability.forProvider("prov-1");
        availability.addSlot(BookableSlot.of(START, 2));
        availability.removeSlot(START);
        assertTrue(availability.slots().isEmpty());
    }
}
