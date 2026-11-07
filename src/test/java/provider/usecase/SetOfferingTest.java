package provider.usecase;

import org.junit.jupiter.api.Test;
import provider.domain.*;
import provider.usecase.dto.SetOfferingRequest;
import provider.usecase.dto.SetOfferingResponse;
import provider.usecase.exception.ProviderNotFoundException;
import provider.usecase.port.out.ProviderRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SetOfferingTest {

    private final ProviderRepository repository = mock(ProviderRepository.class);
    private final SetOffering usecase = new SetOffering(repository);

    @Test
    void setOfferingOnExistingProviderSavesAndReturnsEligibility() {
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

        assertEquals(id.value(), response.providerId());
        assertTrue(response.canReceiveReservations());
        verify(repository).save(provider);
    }

    @Test
    void setOfferingOnNonExistentProviderThrowsNotFound() {
        ProviderId id = ProviderId.generate();
        when(repository.findById(id)).thenReturn(Optional.empty());

        SetOfferingRequest request = new SetOfferingRequest(
                id.value(), List.of(), List.of(), BigDecimal.TEN, 1, 1, 1, "FLEXIBLE", false);

        assertThrows(ProviderNotFoundException.class, () -> usecase.set(request));
    }
}
