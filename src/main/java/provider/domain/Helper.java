package provider.domain;

import java.util.Objects;

/**
 * A helper managed by a provider. Must be KYC-verified to be added.
 */
public final class Helper {

    private final String name;
    private final KycStatus kycStatus;

    private Helper(String name, KycStatus kycStatus) {
        this.name = Objects.requireNonNull(name);
        this.kycStatus = Objects.requireNonNull(kycStatus);
    }

    public static Helper of(String name, KycStatus kycStatus) {
        return new Helper(name, kycStatus);
    }

    public String name() { return name; }
    public KycStatus kycStatus() { return kycStatus; }
}
