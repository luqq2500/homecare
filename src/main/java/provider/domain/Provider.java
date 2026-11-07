package provider.domain;

import provider.domain.exception.ProviderNotVerifiedException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Aggregate root for a Homa provider.
 * Every provider is an individual. Helpers are managed here.
 */
public class Provider {

    private final ProviderId id;
    private final String profileName;
    private KycStatus kycStatus;
    private final List<Helper> helpers = new ArrayList<>();
    private Offering offering;
    private int cancellationCount;
    private boolean hasUnpaidFine;

    private Provider(ProviderId id, String profileName) {
        this.id = Objects.requireNonNull(id);
        this.profileName = Objects.requireNonNull(profileName);
        this.kycStatus = KycStatus.PENDING;
        this.cancellationCount = 0;
        this.hasUnpaidFine = false;
    }

    public static Provider register(ProviderId id, String profileName) {
        return new Provider(id, profileName);
    }

    public void markKycVerified() {
        this.kycStatus = KycStatus.VERIFIED;
    }

    public void markKycRejected() {
        this.kycStatus = KycStatus.REJECTED;
    }

    public void addHelper(Helper helper) {
        Objects.requireNonNull(helper);
        if (helper.kycStatus() != KycStatus.VERIFIED) {
            throw new ProviderNotVerifiedException("Helper must be KYC-verified before being added");
        }
        this.helpers.add(helper);
    }

    public void setOffering(Offering offering) {
        this.offering = Objects.requireNonNull(offering);
    }

    public void recordCancellation() {
        this.cancellationCount++;
    }

    public void applyFine() {
        this.hasUnpaidFine = true;
    }

    public void clearFine() {
        this.hasUnpaidFine = false;
    }

    public boolean canReceiveNewReservations() {
        return kycStatus == KycStatus.VERIFIED
                && offering != null
                && !hasUnpaidFine;
    }

    public ProviderId id() { return id; }
    public String profileName() { return profileName; }
    public KycStatus kycStatus() { return kycStatus; }
    public List<Helper> helpers() { return Collections.unmodifiableList(helpers); }
    public Offering offering() { return offering; }
    public int cancellationCount() { return cancellationCount; }
    public boolean hasUnpaidFine() { return hasUnpaidFine; }
}
