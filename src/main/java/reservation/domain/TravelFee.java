package reservation.domain;

import java.math.BigDecimal;
import java.util.Objects;

public final class TravelFee {

    public static final TravelFee ZERO = new TravelFee(BigDecimal.ZERO);

    private final BigDecimal amount;

    private TravelFee(BigDecimal amount) {
        this.amount = Objects.requireNonNull(amount);
    }

    public static TravelFee of(BigDecimal amount) {
        return new TravelFee(amount);
    }

    public BigDecimal amount() { return amount; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TravelFee travelFee = (TravelFee) o;
        return amount.compareTo(travelFee.amount) == 0;
    }

    @Override
    public int hashCode() {
        return amount.hashCode();
    }
}
