package availability.domain;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Objects;

public final class WeeklySlot {

    private final DayOfWeek day;
    private final LocalTime start;
    private final LocalTime end;

    private WeeklySlot(DayOfWeek day, LocalTime start, LocalTime end) {
        this.day = Objects.requireNonNull(day);
        this.start = Objects.requireNonNull(start);
        this.end = Objects.requireNonNull(end);
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("End must be after start");
        }
    }

    public static WeeklySlot of(DayOfWeek day, LocalTime start, LocalTime end) {
        return new WeeklySlot(day, start, end);
    }

    public DayOfWeek day() { return day; }
    public LocalTime start() { return start; }
    public LocalTime end() { return end; }

    public boolean covers(LocalTime time) {
        return !time.isBefore(start) && time.isBefore(end);
    }
}
