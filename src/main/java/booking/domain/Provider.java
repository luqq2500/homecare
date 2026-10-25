package booking.domain;

import booking.domain.exception.ProviderNotEligibleToCreateBookingConfirmation;

public class Provider {
    private final String id;
    private ProviderStatus status;
    public Provider(String providerId, ProviderStatus status){
        this.id = providerId;
        this.status = status;
    }
    public String getId() {
        return id;
    }

    public void isEligibleToCreateBookingConfirmation() throws RuntimeException {
        if (this.status != ProviderStatus.ACTIVE){
            throw new ProviderNotEligibleToCreateBookingConfirmation("Provider is not eligible to create a booking confirmation.");
        }
    }
}
