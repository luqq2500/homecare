package provider.usecase.port.out;

import provider.domain.Provider;
import provider.domain.ProviderId;

import java.util.Optional;

public interface ProviderRepository {
    void save(Provider provider);
    Optional<Provider> findById(ProviderId id);
}
