package provider.domain;

import java.util.Objects;
import java.util.UUID;

public final class ProviderId {

    private final String value;

    private ProviderId(String value) {
        this.value = Objects.requireNonNull(value);
    }

    public static ProviderId generate() {
        return new ProviderId(UUID.randomUUID().toString());
    }

    public static ProviderId of(String value) {
        return new ProviderId(value);
    }

    public String value() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProviderId that = (ProviderId) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() { return value.hashCode(); }

    @Override
    public String toString() { return value; }
}
