package reservation.domain;

import java.math.BigDecimal;
import java.util.Objects;

public final class HoursFee {

    private final BigDecimal amount;

    private HoursFee(BigDecimal amount) {
        this.amount = Objects.requireNonNull(amount);
    }

    public static HoursFee of(BigDecimal hourlyRate, int numberOfHelpers, int bookedHours) {
        BigDecimal total = hourlyRate
                .multiply(BigDecimal.valueOf(numberOfHelpers))
                .multiply(BigDecimal.valueOf(bookedHours));
        return new HoursFee(total);
    }

    public BigDecimal amount() { return amount; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HoursFee hoursFee = (HoursFee) o;
        return amount.compareTo(hoursFee.amount) == 0;
    }

    @Override
    public int hashCode() {
        return amount.hashCode();
    }
}
