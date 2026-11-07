package reservation.domain;

import reservation.domain.exception.InvalidDurationException;
import reservation.domain.exception.StartTimeNotOn15MinBoundaryException;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Objects;

public final class BookedWindow {

    private final Instant start;
    private final int hours; // 1-4

    private BookedWindow(Instant start, int hours) {
        if (hours < 1 || hours > 4) {
            throw new InvalidDurationException("Duration must be between 1 and 4 whole hours");
        }
        ZonedDateTime zdt = start.atZone(ZoneOffset.UTC);
        if (zdt.getMinute() % 15 != 0 || zdt.getSecond() != 0 || zdt.getNano() != 0) {
            throw new StartTimeNotOn15MinBoundaryException("Start time must be on a 15-minute boundary");
        }
        this.start = start;
        this.hours = hours;
    }

    public static BookedWindow of(Instant start, int hours) {
        return new BookedWindow(start, hours);
    }

    public Instant start() { return start; }
    public int hours() { return hours; }
    public Instant end() { return start.plusSeconds(hours * 3600L); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookedWindow that = (BookedWindow) o;
        return hours == that.hours && start.equals(that.start);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, hours);
    }
}
