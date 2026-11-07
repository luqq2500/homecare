package shared;

public final class DurationHours {

    private final int hours;

    private DurationHours(int hours) {
        if (hours < 1 || hours > 4) {
            throw new IllegalArgumentException("Duration must be between 1 and 4 hours");
        }
        this.hours = hours;
    }

    public static DurationHours of(int hours) {
        return new DurationHours(hours);
    }

    public int hours() { return hours; }
}
