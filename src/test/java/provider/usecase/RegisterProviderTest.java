package provider.usecase;

import org.junit.jupiter.api.Test;
import provider.domain.Provider;
import provider.domain.ProviderId;
import provider.usecase.dto.RegisterProviderRequest;
import provider.usecase.dto.RegisterProviderResponse;
import provider.usecase.port.out.ProviderRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RegisterProviderTest {

    private final ProviderRepository repository = mock(ProviderRepository.class);
    private final RegisterProvider usecase = new RegisterProvider(repository);

    @Test
    void registerCreatesProviderInPendingStatus() {
        RegisterProviderResponse response = usecase.register(new RegisterProviderRequest("Aina"));

        assertNotNull(response.providerId());
        assertEquals("PENDING", response.status());
        verify(repository).save(any(Provider.class));
    }
}
