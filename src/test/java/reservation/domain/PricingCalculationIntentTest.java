package reservation.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PricingCalculationIntentTest {

    @Test
    void standardBookingWithSeparateTravelFee() {
        HoursFee hours = HoursFee.of(new BigDecimal("25.00"), 1, 2);
        TravelFee travel = TravelFee.of(new BigDecimal("10.00"));
        assertEquals(0, new BigDecimal("50.00").compareTo(hours.amount()));
        assertEquals(0, new BigDecimal("60.00").compareTo(hours.amount().add(travel.amount())));
    }

    @Test
    void bookingWithTravelFeeIncludedInPrice() {
        HoursFee hours = HoursFee.of(new BigDecimal("35.00"), 1, 2);
        assertEquals(0, new BigDecimal("70.00").compareTo(hours.amount()));
    }

    @Test
    void multiHelperMultiHourSeparateTravel() {
        HoursFee hours = HoursFee.of(new BigDecimal("30.00"), 2, 3);
        TravelFee travel = TravelFee.of(new BigDecimal("15.00"));
        assertEquals(0, new BigDecimal("180.00").compareTo(hours.amount()));
        assertEquals(0, new BigDecimal("195.00").compareTo(hours.amount().add(travel.amount())));
    }
}
