package provider.domain;

import org.junit.jupiter.api.Test;
import provider.domain.exception.ProviderNotVerifiedException;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProviderTest {

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
    void registerCreatesProviderInPendingStatus() {
        Provider provider = newProvider();
        assertEquals(KycStatus.PENDING, provider.kycStatus());
        assertEquals(0, provider.cancellationCount());
        assertFalse(provider.hasUnpaidFine());
        assertFalse(provider.canReceiveNewReservations());
    }

    @Test
    void markKycVerifiedAllowsReservationsWhenOfferingSetAndNoFine() {
        Provider provider = newProvider();
        provider.markKycVerified();
        provider.setOffering(validOffering());
        assertTrue(provider.canReceiveNewReservations());
    }

    @Test
    void unpaidFineBlocksNewReservations() {
        Provider provider = newProvider();
        provider.markKycVerified();
        provider.setOffering(validOffering());
        provider.applyFine();
        assertFalse(provider.canReceiveNewReservations());
        provider.clearFine();
        assertTrue(provider.canReceiveNewReservations());
    }

    @Test
    void addHelperRequiresVerifiedKyc() {
        Provider provider = newProvider();
        assertThrows(ProviderNotVerifiedException.class,
                () -> provider.addHelper(Helper.of("Bob", KycStatus.PENDING)));
        provider.addHelper(Helper.of("Bob", KycStatus.VERIFIED));
        assertEquals(1, provider.helpers().size());
    }

    @Test
    void recordCancellationIncrementsCount() {
        Provider provider = newProvider();
        provider.recordCancellation();
        provider.recordCancellation();
        assertEquals(2, provider.cancellationCount());
    }

    @Test
    void markKycRejectedSetsStatus() {
        Provider provider = newProvider();
        provider.markKycRejected();
        assertEquals(KycStatus.REJECTED, provider.kycStatus());
        assertFalse(provider.canReceiveNewReservations());
    }
}
