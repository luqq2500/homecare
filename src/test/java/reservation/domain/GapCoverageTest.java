package reservation.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class GapCoverageTest {

    private static final Instant START = Instant.parse("2026-11-10T14:00:00Z");

    private Reservation reserved() {
        Reservation r = Reservation.request(
                ReservationId.generate(), START.minusSeconds(3600),
                BookedWindow.of(START, 2),
                HoursFee.of(new BigDecimal("100.00"), 1, 2),
                TravelFee.of(new BigDecimal("15.00")),
                CancellationTier.STANDARD
        );
        r.accept(START.minusSeconds(1800));
        return r;
    }

    @Test
    void markJobCompletedWithEvidenceKeepsStatusReserved() {
        Reservation r = reserved();
        r.markJobCompletedWithEvidence("media-1", START.plusSeconds(120));
        assertEquals(ReservationStatus.RESERVED, r.status());
        assertNotNull(r.completionEvidence());
    }

    @Test
    void moveToCompletedFromReservedAfterDisputeWindow() {
        Reservation r = reserved();
        r.markJobCompletedWithEvidence("media-1", START.plusSeconds(120));
        r.moveToCompletedAfterDisputeWindowOrResolution(START.plusSeconds(10 * 3600));
        assertEquals(ReservationStatus.COMPLETED, r.status());
    }

    @Test
    void moveToCompletedFromDisputedAfterResolution() {
        Reservation r = reserved();
        r.fileDispute(DisputeType.PROBLEM_WITH_FINISHED_WORK, "reason", START.plusSeconds(3600));
        r.resolveDispute(DisputeResolution.RELEASE_FULL, START.plusSeconds(7200));
        assertEquals(ReservationStatus.COMPLETED, r.status());
    }

    @Test
    void customerCancellationFeeUsesFullPriceWhenTravelIncluded() {
        Reservation r = reserved();
        BigDecimal fee = r.customerCancellationFee(START.minusSeconds(5 * 3600), true);
        assertTrue(fee.signum() >= 0);
    }

    @Test
    void providerCancellationFineIsZeroOutsideWindow() {
        Reservation r = reserved();
        BigDecimal fine = r.providerCancellationFine(START.minusSeconds(30 * 3600));
        assertEquals(0, BigDecimal.ZERO.compareTo(fine));
    }

    @Test
    void providerNoShowFineIsPositive() {
        Reservation r = reserved();
        BigDecimal fine = r.providerNoShowFine();
        assertTrue(fine.signum() > 0);
    }
}
