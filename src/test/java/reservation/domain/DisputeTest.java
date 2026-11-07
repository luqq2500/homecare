package reservation.domain;

import org.junit.jupiter.api.Test;
import reservation.domain.exception.InvalidStatusTransitionException;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class DisputeTest {

    private static final Instant NOW = Instant.parse("2026-11-07T12:00:00Z");
    private static final Instant START = Instant.parse("2026-11-08T14:00:00Z");

    private Reservation reserved() {
        Reservation r = Reservation.request(
                ReservationId.generate(), NOW,
                BookedWindow.of(START, 2),
                HoursFee.of(new BigDecimal("25.00"), 1, 2),
                TravelFee.ZERO,
                CancellationTier.STANDARD
        );
        r.accept(NOW.plusSeconds(60));
        return r;
    }

    @Test
    void fileDisputeFromReservedTransitionsToDisputedAndStoresReport() {
        Reservation r = reserved();
        r.fileDispute(DisputeType.PROBLEM_DURING_JOB, "Missing helper", NOW.plusSeconds(3600));
        assertEquals(ReservationStatus.DISPUTED, r.status());
        assertNotNull(r.dispute());
        assertEquals(DisputeType.PROBLEM_DURING_JOB, r.dispute().type());
        assertFalse(r.dispute().isResolved());
    }

    @Test
    void secondDisputeOnSameReservationIsRejected() {
        Reservation r = reserved();
        r.fileDispute(DisputeType.PROBLEM_DURING_JOB, "First", NOW.plusSeconds(60));
        assertThrows(InvalidStatusTransitionException.class,
                () -> r.fileDispute(DisputeType.PROBLEM_WITH_FINISHED_WORK, "Second", NOW.plusSeconds(120)));
    }

    @Test
    void resolveDisputeMovesToCompleted() {
        Reservation r = reserved();
        r.fileDispute(DisputeType.PROBLEM_WITH_FINISHED_WORK, "Poor finish", NOW.plusSeconds(60));
        r.resolveDispute(DisputeResolution.PARTIAL_REFUND, NOW.plusSeconds(120));
        assertEquals(ReservationStatus.COMPLETED, r.status());
        assertTrue(r.dispute().isResolved());
        assertEquals(DisputeResolution.PARTIAL_REFUND, r.dispute().resolution());
    }

    @Test
    void fileDisputeFromNonReservedIsRejected() {
        Reservation r = Reservation.request(
                ReservationId.generate(), NOW,
                BookedWindow.of(START, 1),
                HoursFee.of(BigDecimal.TEN, 1, 1),
                TravelFee.ZERO,
                CancellationTier.FLEXIBLE
        );
        assertThrows(InvalidStatusTransitionException.class,
                () -> r.fileDispute(DisputeType.PROBLEM_DURING_JOB, "Too early", NOW));
    }

    @Test
    void disputeRequiresReason() {
        assertThrows(IllegalArgumentException.class,
                () -> Dispute.file(DisputeType.PROBLEM_DURING_JOB, "", NOW));
    }
}
