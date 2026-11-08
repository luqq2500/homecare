package provider.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FineDeductionIntentTest {

    @Test
    void applyFineSetsUnpaidFlag() {
        Provider provider = Provider.register(ProviderId.generate(), "Aina");
        assertFalse(provider.hasUnpaidFine());
        provider.applyFine();
        assertTrue(provider.hasUnpaidFine());
    }

    @Test
    void clearFineRemovesUnpaidFlag() {
        Provider provider = Provider.register(ProviderId.generate(), "Aina");
        provider.applyFine();
        provider.clearFine();
        assertFalse(provider.hasUnpaidFine());
    }

    @Test
    void unpaidFineBlocksNewReservations() {
        Provider provider = Provider.register(ProviderId.generate(), "Aina");
        provider.markKycVerified();
        provider.setOffering(Offering.of(
                java.util.List.of("Kitchen"),
                java.util.List.of("Vacuum"),
                new java.math.BigDecimal("25.00"),
                1, 4, 1,
                CancellationTier.STANDARD,
                false
        ));
        assertTrue(provider.canReceiveNewReservations());
        provider.applyFine();
        assertFalse(provider.canReceiveNewReservations());
    }
}
