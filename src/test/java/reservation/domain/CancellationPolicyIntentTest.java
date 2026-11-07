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
    void customerCancelBeforeResponseReleasesWithZeroFee() {
        for (CancellationTier tier : CancellationTier.values()) {
            Reservation r = Reservation.request(
                    ReservationId.generate(), NOW,
                    BookedWindow.of(START, 2),
                    HoursFee.of(new BigDecimal("100.00"), 1, 2),
                    TravelFee.of(new BigDecimal("15.00")),
                    tier
            );
            r.cancelByCustomer(NOW.plusSeconds(30));
            assertEquals(ReservationStatus.CANCELLED, r.status());
            assertEquals(0, BigDecimal.ZERO.compareTo(r.customerCancellationFee(NOW.plusSeconds(30))));
        }
    }

    @Test
    void flexibleCancelBeforeStartRefundsFull() {
        Reservation r = reservedWithTier(CancellationTier.FLEXIBLE);
        Instant cancelAt = START.minusSeconds(3600);
        BigDecimal fee = r.customerCancellationFee(cancelAt);
        assertEquals(0, BigDecimal.ZERO.compareTo(fee));
    }

    @Test
    void standardCancelWithinFeeWindowGivesTenPercentOfHours() {
        Reservation r = reservedWithTier(CancellationTier.STANDARD);
        Instant cancelAt = START.minusSeconds(5 * 3600);
        BigDecimal fee = r.customerCancellationFee(cancelAt);
        assertEquals(0, new BigDecimal("10.00").compareTo(fee));
    }

    @Test
    void firmCancelWithinFeeWindowGivesTwentyPercentOfHours() {
        Reservation r = reservedWithTier(CancellationTier.FIRM);
        Instant cancelAt = START.minusSeconds(5 * 3600);
        BigDecimal fee = r.customerCancellationFee(cancelAt);
        assertEquals(0, new BigDecimal("20.00").compareTo(fee));
    }

    @Test
    void providerLateCancellationFineIsTwentyPercentOfHours() {
        Reservation r = reservedWithTier(CancellationTier.STANDARD);
        Instant cancelAt = START.minusSeconds(12 * 3600);
        BigDecimal fine = r.providerCancellationFine(cancelAt);
        assertEquals(0, new BigDecimal("20.00").compareTo(fine));
    }

    @Test
    void providerNoShowFineIsThirtyPercentOfHours() {
        Reservation r = reservedWithTier(CancellationTier.STANDARD);
        BigDecimal fine = r.providerNoShowFine();
        assertEquals(0, new BigDecimal("30.00").compareTo(fine));
    }
}
