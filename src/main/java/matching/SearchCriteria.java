package matching;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public final class SearchCriteria {

    private final List<String> jobs;
    private final Instant start;
    private final int durationHours;
    private final int helperCount;

    private SearchCriteria(List<String> jobs, Instant start, int durationHours, int helperCount) {
        this.jobs = List.copyOf(Objects.requireNonNull(jobs));
        this.start = Objects.requireNonNull(start);
        if (durationHours < 1 || durationHours > 4) {
            throw new IllegalArgumentException("Duration must be between 1 and 4 hours");
        }
        if (helperCount < 1 || helperCount > 10) {
            throw new IllegalArgumentException("Helper count must be between 1 and 10");
        }
        if (start.getEpochSecond() % (15 * 60) != 0) {
            throw new IllegalArgumentException("Start must be on a 15-minute boundary");
        }
        this.durationHours = durationHours;
        this.helperCount = helperCount;
    }

    public static SearchCriteria of(List<String> jobs, Instant start, int durationHours, int helperCount) {
        return new SearchCriteria(jobs, start, durationHours, helperCount);
    }

    public List<String> jobs() { return jobs; }
    public Instant start() { return start; }
    public int durationHours() { return durationHours; }
    public int helperCount() { return helperCount; }
}
