package availability.domain;

import java.time.Instant;
import java.util.Objects;

/**
 * A contiguous bookable window. Start must be on a 15-minute boundary.
 * Duration is whole hours between 1 and 4.
 */
public final class BookableSlot {

    private final Instant start;
    private final Instant end;

    private BookableSlot(Instant start, Instant end) {
        this.start = Objects.requireNonNull(start);
        this.end = Objects.requireNonNull(end);
    }

    public static BookableSlot of(Instant start, int durationHours) {
        if (durationHours < 1 || durationHours > 4) {
            throw new IllegalArgumentException("Duration must be between 1 and 4 hours");
        }
        if (start.getEpochSecond() % (15 * 60) != 0) {
            throw new IllegalArgumentException("Start must be on a 15-minute boundary");
        }
        return new BookableSlot(start, start.plusSeconds(durationHours * 3600L));
    }

    public boolean overlaps(BookableSlot other) {
        return this.start.isBefore(other.end) && other.start.isBefore(this.end);
    }

    public Instant start() { return start; }
    public Instant end() { return end; }
}
