package booking.application.port;

import booking.domain.Provider;

import java.util.Optional;

public interface ProviderRepository {
    Optional<Provider> findById(String providerId);
}
