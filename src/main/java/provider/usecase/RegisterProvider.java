package provider.usecase;

import provider.domain.Provider;
import provider.domain.ProviderId;
import provider.usecase.dto.RegisterProviderRequest;
import provider.usecase.dto.RegisterProviderResponse;
import provider.usecase.port.in.ProviderRegistrar;
import provider.usecase.port.out.ProviderRepository;

public class RegisterProvider implements ProviderRegistrar {

    private final ProviderRepository repository;

    public RegisterProvider(ProviderRepository repository) {
        this.repository = repository;
    }

    @Override
    public RegisterProviderResponse register(RegisterProviderRequest request) {
        ProviderId id = ProviderId.generate();
        Provider provider = Provider.register(id, request.profileName());
        repository.save(provider);
        return new RegisterProviderResponse(id.value(), provider.kycStatus().name());
    }
}
