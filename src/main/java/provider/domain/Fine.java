package provider.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public final class Fine {

    private final String reservationId;
    private final BigDecimal amount;
    private final Instant appliedAt;
    private boolean waived;

    private Fine(String reservationId, BigDecimal amount, Instant appliedAt) {
        this.reservationId = Objects.requireNonNull(reservationId);
        this.amount = Objects.requireNonNull(amount);
        this.appliedAt = Objects.requireNonNull(appliedAt);
        this.waived = false;
    }

    public static Fine of(String reservationId, BigDecimal amount, Instant appliedAt) {
        if (amount.signum() <= 0) {
            throw new IllegalArgumentException("Fine amount must be positive");
        }
        return new Fine(reservationId, amount, appliedAt);
    }

    public void waive() {
        this.waived = true;
    }

    public String reservationId() { return reservationId; }
    public BigDecimal amount() { return amount; }
    public Instant appliedAt() { return appliedAt; }
    public boolean isWaived() { return waived; }

    public BigDecimal outstanding() {
        return waived ? BigDecimal.ZERO : amount;
    }
}
