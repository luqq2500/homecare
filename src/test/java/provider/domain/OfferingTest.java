package provider.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OfferingTest {

    @Test
    void offeringAcceptsValidRange() {
        Offering offering = Offering.of(
                List.of("Laundry"),
                List.of("Iron"),
                new BigDecimal("20.00"),
                1, 4, 1,
                CancellationTier.FLEXIBLE,
                true
        );
        assertEquals(1, offering.minHours());
        assertEquals(4, offering.maxHours());
        assertEquals(1, offering.helperCount());
        assertTrue(offering.travelFeeIncluded());
        assertEquals(CancellationTier.FLEXIBLE, offering.cancellationTier());
    }

    @Test
    void offeringRejectsInvalidHoursOrHelperCount() {
        assertThrows(IllegalArgumentException.class, () ->
                Offering.of(List.of("Job"), List.of(), BigDecimal.TEN, 0, 2, 1, CancellationTier.STANDARD, false));
        assertThrows(IllegalArgumentException.class, () ->
                Offering.of(List.of("Job"), List.of(), BigDecimal.TEN, 1, 5, 1, CancellationTier.STANDARD, false));
        assertThrows(IllegalArgumentException.class, () ->
                Offering.of(List.of("Job"), List.of(), BigDecimal.TEN, 1, 2, 0, CancellationTier.STANDARD, false));
        assertThrows(IllegalArgumentException.class, () ->
                Offering.of(List.of("Job"), List.of(), BigDecimal.TEN, 1, 2, 11, CancellationTier.STANDARD, false));
    }
}
