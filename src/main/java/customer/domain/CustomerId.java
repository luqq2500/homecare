package customer.domain;

import java.util.Objects;

public final class CustomerId {

    private final String value;

    private CustomerId(String value) {
        this.value = Objects.requireNonNull(value);
    }

    public static CustomerId of(String value) {
        return new CustomerId(value);
    }

    public static CustomerId generate() {
        return new CustomerId(java.util.UUID.randomUUID().toString());
    }

    public String value() { return value; }
}
