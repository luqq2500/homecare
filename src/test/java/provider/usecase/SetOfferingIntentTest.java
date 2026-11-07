package provider.usecase;

import org.junit.jupiter.api.Test;
import provider.domain.*;
import provider.usecase.dto.SetOfferingRequest;
import provider.usecase.dto.SetOfferingResponse;
import provider.usecase.port.out.ProviderRepository;
import provider.usecase.port.out.ProviderIEPublisher;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SetOfferingIntentTest {

    private final ProviderRepository repository = mock(ProviderRepository.class);
    private final ProviderIEPublisher publisher = mock(ProviderIEPublisher.class);
    private final SetOffering usecase = new SetOffering(repository, publisher);

    @Test
    void offeringConfigurationPublishesProviderOfferingUpdatedIE() {
        ProviderId id = ProviderId.generate();
        Provider provider = Provider.register(id, "Aina");
        provider.markKycVerified();
        when(repository.findById(id)).thenReturn(Optional.of(provider));

        SetOfferingRequest request = new SetOfferingRequest(
                id.value(),
                List.of("Kitchen care"),
                List.of("Vacuum"),
                new BigDecimal("25.00"),
                1, 4, 2,
                "STANDARD",
                false
        );

        SetOfferingResponse response = usecase.set(request);

        assertTrue(response.canReceiveReservations());
        verify(publisher).publish(any());
        verify(repository).save(provider);
    }
}
