package provider.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class FineDeductionIntentTest {

    @Test
    void outstandingFinesAccumulate() {
        Provider provider = Provider.register(ProviderId.generate(), "Aina");
        provider.applyFine("res-1", new BigDecimal("30.00"), Instant.now());
        provider.applyFine("res-2", new BigDecimal("20.00"), Instant.now());
        assertEquals(0, new BigDecimal("50.00").compareTo(provider.outstandingFines()));
    }

    @Test
    void waiveClearsSpecificFine() {
        Provider provider = Provider.register(ProviderId.generate(), "Aina");
        provider.applyFine("res-1", new BigDecimal("30.00"), Instant.now());
        provider.waiveFine("res-1");
        assertEquals(0, BigDecimal.ZERO.compareTo(provider.outstandingFines()));
    }

    @Test
    void unpaidFineBlocksLive() {
        Provider provider = Provider.register(ProviderId.generate(), "Aina");
        provider.markKycVerified();
        provider.applyFine("res-1", new BigDecimal("30.00"), Instant.now());
        assertTrue(provider.hasUnpaidFine());
        assertFalse(provider.isLive());
    }
}
