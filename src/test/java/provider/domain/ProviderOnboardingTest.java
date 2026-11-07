package provider.domain;

import org.junit.jupiter.api.Test;
import provider.domain.exception.ProviderNotVerifiedException;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProviderOnboardingTest {

    private Provider newProvider() {
        return Provider.register(ProviderId.generate(), "Aina");
    }

    private Offering validOffering() {
        return Offering.of(
                List.of("Kitchen care"),
                List.of("Vacuum"),
                new BigDecimal("25.00"),
                1, 4, 2,
                CancellationTier.STANDARD,
                false
        );
    }

    @Test
    void successfulOnboardingWithoutExtraHelpersSetsLiveAndVerified() {
        Provider provider = newProvider();
        provider.markKycVerified();
        provider.setOffering(validOffering());
        assertEquals(KycStatus.VERIFIED, provider.kycStatus());
        assertTrue(provider.canReceiveNewReservations());
    }

    @Test
    void cannotGoLiveWithoutEkyc() {
        Provider provider = newProvider();
        provider.setOffering(validOffering());
        assertEquals(KycStatus.PENDING, provider.kycStatus());
        assertFalse(provider.canReceiveNewReservations());
    }

    @Test
    void addingHelperRequiresEkyc() {
        Provider provider = newProvider();
        provider.markKycVerified();
        assertThrows(ProviderNotVerifiedException.class,
                () -> provider.addHelper(Helper.of("Helper A", KycStatus.PENDING)));
        provider.addHelper(Helper.of("Helper A", KycStatus.VERIFIED));
        assertEquals(1, provider.helpers().size());
    }
}
