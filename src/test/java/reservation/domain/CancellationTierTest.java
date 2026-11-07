package reservation.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CancellationTierTest {

    @Test
    void feePercent_matchesSpecification() {
        assertEquals(0, CancellationTier.FLEXIBLE.feePercent());
        assertEquals(10, CancellationTier.STANDARD.feePercent());
        assertEquals(20, CancellationTier.FIRM.feePercent());
    }
}
