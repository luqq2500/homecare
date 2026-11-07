package provider.usecase.port.in;

import provider.usecase.dto.RegisterProviderRequest;
import provider.usecase.dto.RegisterProviderResponse;

public interface ProviderRegistrar {
    RegisterProviderResponse register(RegisterProviderRequest request);
}
