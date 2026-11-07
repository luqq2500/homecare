package reservation.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class JobCompletionAndDisputeIntentTest {

    private static final Instant NOW = Instant.parse("2026-11-09T10:00:00Z");
    private static final Instant START = Instant.parse("2026-11-10T10:00:00Z");

    private Reservation reserved() {
        Reservation r = Reservation.request(
                ReservationId.generate(), NOW,
                BookedWindow.of(START, 2),
                HoursFee.of(new BigDecimal("100.00"), 1, 2),
                TravelFee.of(new BigDecimal("15.00")),
                CancellationTier.STANDARD
        );
        r.accept(NOW.plusSeconds(60));
        return r;
    }

    @Test
    void markJobCompletedWithEvidenceKeepsStatusReserved() {
        Reservation r = reserved();
        r.markJobCompletedWithEvidence("media-1", START.plusSeconds(2 * 3600 + 300));
        assertEquals(ReservationStatus.RESERVED, r.status());
        assertNotNull(r.completionEvidence());
    }

    @Test
    void moveToCompletedAfterDisputeWindowTransitionsToCompleted() {
        Reservation r = reserved();
        r.markJobCompletedWithEvidence("media-1", START.plusSeconds(2 * 3600));
        r.moveToCompletedAfterDisputeWindowOrResolution(START.plusSeconds(3 * 3600));
        assertEquals(ReservationStatus.COMPLETED, r.status());
    }

    @Test
    void fileDisputeTransitionsToDisputed() {
        Reservation r = reserved();
        r.fileDispute(DisputeType.PROBLEM_WITH_FINISHED_WORK, "Missing helper and incomplete work", START.plusSeconds(2 * 3600 + 1800));
        assertEquals(ReservationStatus.DISPUTED, r.status());
        assertNotNull(r.dispute());
    }

    @Test
    void resolveDisputeTransitionsToCompleted() {
        Reservation r = reserved();
        r.fileDispute(DisputeType.PROBLEM_WITH_FINISHED_WORK, "Incomplete work", START.plusSeconds(2 * 3600));
        r.resolveDispute(DisputeResolution.PARTIAL_REFUND, START.plusSeconds(3 * 3600));
        assertEquals(ReservationStatus.COMPLETED, r.status());
    }
}
