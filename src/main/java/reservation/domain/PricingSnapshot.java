package reservation.domain;

import java.math.BigDecimal;
import java.util.Objects;

public final class PricingSnapshot {

    private final BigDecimal hourlyRate;
    private final int helpers;
    private final int hours;
    private final boolean travelIncluded;
    private final BigDecimal travelFee;
    private final CancellationTier tier;

    private PricingSnapshot(BigDecimal hourlyRate, int helpers, int hours,
                            boolean travelIncluded, BigDecimal travelFee, CancellationTier tier) {
        this.hourlyRate = Objects.requireNonNull(hourlyRate);
        this.helpers = helpers;
        this.hours = hours;
        this.travelIncluded = travelIncluded;
        this.travelFee = travelIncluded ? BigDecimal.ZERO : Objects.requireNonNull(travelFee);
        this.tier = Objects.requireNonNull(tier);
    }

    public static PricingSnapshot of(BigDecimal hourlyRate, int helpers, int hours,
                                     boolean travelIncluded, BigDecimal travelFee, CancellationTier tier) {
        return new PricingSnapshot(hourlyRate, helpers, hours, travelIncluded, travelFee, tier);
    }

    public BigDecimal hourlyRate() { return hourlyRate; }
    public int helpers() { return helpers; }
    public int hours() { return hours; }
    public boolean travelIncluded() { return travelIncluded; }
    public BigDecimal travelFee() { return travelFee; }
    public CancellationTier tier() { return tier; }
}
