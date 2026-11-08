package provider.domain.availability;

import java.time.Instant;
import java.util.Objects;

public final class BookableSlot {

    private final Instant start;
    private final int durationHours;

    private BookableSlot(Instant start, int durationHours) {
        if (durationHours < 1 || durationHours > 4) {
            throw new IllegalArgumentException("Duration must be 1-4 hours");
        }
        this.start = Objects.requireNonNull(start);
        this.durationHours = durationHours;
    }

    public static BookableSlot of(Instant start, int durationHours) {
        return new BookableSlot(start, durationHours);
    }

    public Instant start() { return start; }
    public int durationHours() { return durationHours; }
    public Instant end() { return start.plusSeconds(durationHours * 3600L); }

    public boolean overlaps(BookableSlot other) {
        return start.isBefore(other.end()) && other.start.isBefore(end());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookableSlot that = (BookableSlot) o;
        return durationHours == that.durationHours && start.equals(that.start);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, durationHours);
    }
}
