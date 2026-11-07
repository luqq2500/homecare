package reservation.domain;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class ReservationCompensationTest {

    private static final Instant NOW = Instant.parse("2026-11-07T12:00:00Z");
    private static final Instant FUTURE_START = Instant.parse("2026-11-08T14:00:00Z");

    private Reservation newRequested() {
        return Reservation.request(
                ReservationId.generate(),
                NOW,
                BookedWindow.of(FUTURE_START, 2),
                HoursFee.of(new BigDecimal("100.00"), 1, 2),
                TravelFee.of(new BigDecimal("10.00")),
                CancellationTier.STANDARD
        );
    }

    @Test
    void customerNoShowCompensationIsTravelPlusThirtyPercentHours() {
        Reservation r = newRequested();
        BigDecimal compensation = r.customerNoShowCompensation();
        assertEquals(0, new BigDecimal("70.00").compareTo(compensation));
    }

    @Test
    void customerCancellationFeeUsesFullPriceWhenTravelIncluded() {
        Reservation r = Reservation.request(
                ReservationId.generate(), NOW,
                BookedWindow.of(FUTURE_START, 2),
                HoursFee.of(new BigDecimal("100.00"), 1, 2),
                TravelFee.of(new BigDecimal("10.00")),
                CancellationTier.FIRM
        );
        Instant inside = FUTURE_START.minusSeconds(12 * 3600);
        BigDecimal fee = r.customerCancellationFee(inside, true);
        assertEquals(0, new BigDecimal("42.00").compareTo(fee));
    }

    @Test
    void hoursFeeRejectsHelpersOutsideOneToTen() {
        assertThrows(IllegalArgumentException.class, () -> HoursFee.of(new BigDecimal("10"), 0, 2));
        assertThrows(IllegalArgumentException.class, () -> HoursFee.of(new BigDecimal("10"), 11, 2));
        assertDoesNotThrow(() -> HoursFee.of(new BigDecimal("10"), 1, 2));
        assertDoesNotThrow(() -> HoursFee.of(new BigDecimal("10"), 10, 2));
    }
}
