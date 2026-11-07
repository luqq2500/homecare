package reservation.domain;

import reservation.domain.exception.InvalidDurationException;
import reservation.domain.exception.StartTimeNotOn15MinBoundaryException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class BookedWindowTest {

    @Test
    void whenStartIsOnFifteenMinuteBoundaryAndDurationIsWithinRangeShouldCreateValidWindow() {
        Instant start = Instant.parse("2026-11-08T14:00:00Z");
        BookedWindow window = BookedWindow.of(start, 2);
        assertEquals(start, window.start());
        assertEquals(2, window.hours());
        assertEquals(start.plusSeconds(2 * 3600), window.end());
    }

    @Test
    void whenDurationIsLessThanOneHourShouldRejectWindow() {
        Instant start = Instant.parse("2026-11-08T14:00:00Z");
        assertThrows(InvalidDurationException.class, () -> BookedWindow.of(start, 0));
    }

    @Test
    void whenDurationExceedsFourHoursShouldRejectWindow() {
        Instant start = Instant.parse("2026-11-08T14:00:00Z");
        assertThrows(InvalidDurationException.class, () -> BookedWindow.of(start, 5));
    }

    @Test
    void whenStartTimeIsNotOnFifteenMinuteBoundaryShouldRejectWindow() {
        Instant start = Instant.parse("2026-11-08T14:07:00Z");
        assertThrows(StartTimeNotOn15MinBoundaryException.class, () -> BookedWindow.of(start, 2));
    }

    @Test
    void whenStartTimeContainsSecondsShouldRejectWindow() {
        Instant start = Instant.parse("2026-11-08T14:00:30Z");
        assertThrows(StartTimeNotOn15MinBoundaryException.class, () -> BookedWindow.of(start, 2));
    }
}
