package payment.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class TravelIncludedPricingIntentTest {

    @Test
    void separateTravelFeeAddsToTotal() {
        BigDecimal hours = new BigDecimal("50.00");
        BigDecimal travel = new BigDecimal("10.00");
        Settlement s = SettlementCalculator.customerCancel(hours, travel, 10, false);
        assertEquals(0, new BigDecimal("60.00").compareTo(s.total()));
    }

    @Test
    void includedTravelFeeTotalEqualsHours() {
        BigDecimal hours = new BigDecimal("70.00");
        BigDecimal travel = BigDecimal.ZERO;
        Settlement s = SettlementCalculator.customerCancel(hours, travel, 10, false);
        assertEquals(0, hours.compareTo(s.total()));
    }

    @Test
    void firmTierTakesTwentyPercentOfHours() {
        BigDecimal hours = new BigDecimal("100.00");
        BigDecimal travel = new BigDecimal("15.00");
        Settlement s = SettlementCalculator.customerCancel(hours, travel, 20, false);
        assertEquals(0, new BigDecimal("20.00").compareTo(s.toProvider()));
    }
}
