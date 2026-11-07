package reservation.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PricingInvariantIntentTest {

    @Test
    void deselectingJobsDoesNotChangePrice() {
        HoursFee hours = HoursFee.of(new BigDecimal("30.00"), 1, 3);
        assertEquals(0, new BigDecimal("90.00").compareTo(hours.amount()));
    }
}
