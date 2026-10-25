package booking.domain;

import booking.domain.exception.InActiveProviderException;

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

    public void validateIsActive() throws RuntimeException {
        if (this.status != ProviderStatus.ACTIVE){
            throw new InActiveProviderException("Provider is no longer active.");
        }
    }
}
