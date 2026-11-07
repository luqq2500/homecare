package provider.domain;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * What a provider offers: jobs, tools, pricing, helper count, hours, cancellation tier, travel fee mode.
 */
public final class Offering {

    private final List<String> jobs;
    private final List<String> tools;
    private final BigDecimal hourlyRatePerHelper;
    private final int minHours;
    private final int maxHours;
    private final int helperCount;
    private final CancellationTier cancellationTier;
    private final boolean travelFeeIncluded;

    private Offering(List<String> jobs,
                     List<String> tools,
                     BigDecimal hourlyRatePerHelper,
                     int minHours,
                     int maxHours,
                     int helperCount,
                     CancellationTier cancellationTier,
                     boolean travelFeeIncluded) {
        this.jobs = List.copyOf(Objects.requireNonNull(jobs));
        this.tools = List.copyOf(Objects.requireNonNull(tools));
        this.hourlyRatePerHelper = Objects.requireNonNull(hourlyRatePerHelper);
        if (minHours < 1 || maxHours > 4 || minHours > maxHours) {
            throw new IllegalArgumentException("Hours must be between 1 and 4, min <= max");
        }
        if (helperCount < 1 || helperCount > 10) {
            throw new IllegalArgumentException("Helper count must be between 1 and 10");
        }
        this.minHours = minHours;
        this.maxHours = maxHours;
        this.helperCount = helperCount;
        this.cancellationTier = Objects.requireNonNull(cancellationTier);
        this.travelFeeIncluded = travelFeeIncluded;
    }

    public static Offering of(List<String> jobs,
                              List<String> tools,
                              BigDecimal hourlyRatePerHelper,
                              int minHours,
                              int maxHours,
                              int helperCount,
                              CancellationTier cancellationTier,
                              boolean travelFeeIncluded) {
        return new Offering(jobs, tools, hourlyRatePerHelper, minHours, maxHours, helperCount, cancellationTier, travelFeeIncluded);
    }

    public List<String> jobs() { return jobs; }
    public List<String> tools() { return tools; }
    public BigDecimal hourlyRatePerHelper() { return hourlyRatePerHelper; }
    public int minHours() { return minHours; }
    public int maxHours() { return maxHours; }
    public int helperCount() { return helperCount; }
    public CancellationTier cancellationTier() { return cancellationTier; }
    public boolean travelFeeIncluded() { return travelFeeIncluded; }
}
