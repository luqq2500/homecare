package reservation.domain;

import java.time.Instant;
import java.util.Objects;

/**
 * One report per reservation.
 */
public final class Dispute {

    private final DisputeType type;
    private final String reason;
    private final Instant filedAt;
    private DisputeResolution resolution;

    private Dispute(DisputeType type, String reason, Instant filedAt) {
        this.type = Objects.requireNonNull(type);
        this.reason = Objects.requireNonNull(reason);
        this.filedAt = Objects.requireNonNull(filedAt);
    }

    public static Dispute file(DisputeType type, String reason, Instant filedAt) {
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Reason is required");
        }
        return new Dispute(type, reason, filedAt);
    }

    public void resolve(DisputeResolution resolution) {
        if (this.resolution != null) {
            throw new IllegalStateException("Dispute already resolved");
        }
        this.resolution = Objects.requireNonNull(resolution);
    }

    public DisputeType type() { return type; }
    public String reason() { return reason; }
    public Instant filedAt() { return filedAt; }
    public DisputeResolution resolution() { return resolution; }
    public boolean isResolved() { return resolution != null; }
}
