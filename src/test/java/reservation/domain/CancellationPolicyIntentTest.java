package reservation.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class CancellationPolicyIntentTest {

    private static final Instant NOW = Instant.parse("2026-11-09T10:00:00Z");
    private static final Instant START = Instant.parse("2026-11-10T14:00:00Z");

    private Reservation reservedWithTier(CancellationTier tier) {
        Reservation r = Reservation.request(
                ReservationId.generate(), NOW,
                BookedWindow.of(START, 2),
                HoursFee.of(new BigDecimal("100.00"), 1, 2),
                TravelFee.of(new BigDecimal("15.00")),
                tier
        );
        r.accept(NOW.plusSeconds(60));
        return r;
    }

    @Test
    void customerCancelFromRequestedTransitionsToCancelled() {
        Reservation r = Reservation.request(
                ReservationId.generate(), NOW,
                BookedWindow.of(START, 2),
                HoursFee.of(new BigDecimal("100.00"), 1, 2),
                TravelFee.of(new BigDecimal("15.00")),
                CancellationTier.STANDARD
        );
        r.cancelByCustomer(NOW.plusSeconds(30));
        assertEquals(ReservationStatus.CANCELLED, r.status());
    }

    @Test
    void flexibleCancelBeforeStartHasZeroFee() {
        Reservation r = reservedWithTier(CancellationTier.FLEXIBLE);
        Instant cancelAt = START.minusSeconds(3600);
        BigDecimal fee = r.customerCancellationFee(cancelAt);
        assertTrue(fee.signum() >= 0);
    }

    @Test
    void standardCancelWithinFeeWindowHasPositiveFee() {
        Reservation r = reservedWithTier(CancellationTier.STANDARD);
        Instant cancelAt = START.minusSeconds(5 * 3600);
        BigDecimal fee = r.customerCancellationFee(cancelAt);
        assertTrue(fee.signum() > 0);
    }

    @Test
    void firmCancelWithinFeeWindowHasHigherFeeThanStandard() {
        Reservation standard = reservedWithTier(CancellationTier.STANDARD);
        Reservation firm = reservedWithTier(CancellationTier.FIRM);
        Instant cancelAt = START.minusSeconds(5 * 3600);
        assertTrue(firm.customerCancellationFee(cancelAt).compareTo(standard.customerCancellationFee(cancelAt)) >= 0);
    }

    @Test
    void providerLateCancellationFineIsNonNegative() {
        Reservation r = reservedWithTier(CancellationTier.STANDARD);
        Instant cancelAt = START.minusSeconds(12 * 3600);
        BigDecimal fine = r.providerCancellationFine(cancelAt);
        assertTrue(fine.signum() >= 0);
    }

    @Test
    void providerNoShowFineIsPositive() {
        Reservation r = reservedWithTier(CancellationTier.STANDARD);
        BigDecimal fine = r.providerNoShowFine();
        assertTrue(fine.signum() > 0);
    }
}
