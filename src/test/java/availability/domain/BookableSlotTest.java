package availability.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class BookableSlotTest {

    private static final Instant ON_BOUNDARY = Instant.parse("2026-11-08T14:00:00Z");
    private static final Instant OFF_BOUNDARY = Instant.parse("2026-11-08T14:07:00Z");

    @Test
    void ofAcceptsValidDurationAndBoundary() {
        BookableSlot slot = BookableSlot.of(ON_BOUNDARY, 2);
        assertEquals(ON_BOUNDARY, slot.start());
        assertEquals(ON_BOUNDARY.plusSeconds(2 * 3600), slot.end());
    }

    @Test
    void ofRejectsDurationOutsideOneToFour() {
        assertThrows(IllegalArgumentException.class, () -> BookableSlot.of(ON_BOUNDARY, 0));
        assertThrows(IllegalArgumentException.class, () -> BookableSlot.of(ON_BOUNDARY, 5));
    }

    @Test
    void ofRejectsStartNotOn15MinBoundary() {
        assertThrows(IllegalArgumentException.class, () -> BookableSlot.of(OFF_BOUNDARY, 1));
    }

    @Test
    void overlapsDetectsPartialAndFullOverlap() {
        BookableSlot a = BookableSlot.of(ON_BOUNDARY, 2);
        BookableSlot b = BookableSlot.of(ON_BOUNDARY.plusSeconds(3600), 2);
        BookableSlot c = BookableSlot.of(ON_BOUNDARY.plusSeconds(3 * 3600), 1);
        assertTrue(a.overlaps(b));
        assertFalse(a.overlaps(c));
    }
}
