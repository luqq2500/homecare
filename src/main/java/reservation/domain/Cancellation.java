package reservation.domain;

import java.time.Instant;
import java.util.Objects;

public final class Cancellation {

    public enum By { CUSTOMER, PROVIDER }
    public enum Reason { REQUEST_CANCEL, LATE_CANCEL, NO_SHOW, CUSTOMER_NO_SHOW }

    private final By by;
    private final Reason reason;
    private final Instant at;

    private Cancellation(By by, Reason reason, Instant at) {
        this.by = Objects.requireNonNull(by);
        this.reason = Objects.requireNonNull(reason);
        this.at = Objects.requireNonNull(at);
    }

    public static Cancellation of(By by, Reason reason, Instant at) {
        return new Cancellation(by, reason, at);
    }

    public By by() { return by; }
    public Reason reason() { return reason; }
    public Instant at() { return at; }
}
