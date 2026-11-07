package reservation.domain;

public enum CancellationTier {
    FLEXIBLE(0),
    STANDARD(10),
    FIRM(20);

    private final int feePercent;

    CancellationTier(int feePercent) {
        this.feePercent = feePercent;
    }

    public int feePercent() {
        return feePercent;
    }
}
