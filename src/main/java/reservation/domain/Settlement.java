package reservation.domain;

import java.math.BigDecimal;
import java.util.Objects;

public final class Settlement {

    private final BigDecimal toProvider;
    private final BigDecimal toCustomer;
    private final BigDecimal toHoma;

    private Settlement(BigDecimal toProvider, BigDecimal toCustomer, BigDecimal toHoma) {
        this.toProvider = Objects.requireNonNull(toProvider);
        this.toCustomer = Objects.requireNonNull(toCustomer);
        this.toHoma = Objects.requireNonNull(toHoma);
    }

    public static Settlement of(BigDecimal toProvider, BigDecimal toCustomer, BigDecimal toHoma) {
        return new Settlement(toProvider, toCustomer, toHoma);
    }

    public BigDecimal toProvider() { return toProvider; }
    public BigDecimal toCustomer() { return toCustomer; }
    public BigDecimal toHoma() { return toHoma; }

    public BigDecimal total() {
        return toProvider.add(toCustomer).add(toHoma);
    }
}
