package shared;

import java.time.Instant;
import java.util.Objects;

public final class TimeWindow {

    private final Instant start;
    private final Instant end;

    private TimeWindow(Instant start, Instant end) {
        this.start = Objects.requireNonNull(start);
        this.end = Objects.requireNonNull(end);
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("End must be after start");
        }
    }

    public static TimeWindow of(Instant start, Instant end) {
        return new TimeWindow(start, end);
    }

    public boolean contains(Instant instant) {
        return !instant.isBefore(start) && instant.isBefore(end);
    }

    public Instant start() { return start; }
    public Instant end() { return end; }
}
