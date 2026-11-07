package reservation.domain;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class ReservationCompletionTest {

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
    void markJobCompletedWithEvidenceKeepsStatusReserved() {
        Reservation r = newRequested();
        r.accept(NOW.plusSeconds(60));
        r.markJobCompletedWithEvidence(NOW.plusSeconds(3600));
        assertEquals(ReservationStatus.RESERVED, r.status());
    }

    @Test
    void moveToCompletedFromReservedAfterDisputeWindow() {
        Reservation r = newRequested();
        r.accept(NOW.plusSeconds(60));
        r.moveToCompletedAfterDisputeWindowOrResolution(NOW.plusSeconds(7200));
        assertEquals(ReservationStatus.COMPLETED, r.status());
    }

    @Test
    void moveToCompletedFromDisputedAfterResolution() {
        Reservation r = newRequested();
        r.accept(NOW.plusSeconds(60));
        r.markDisputed();
        r.moveToCompletedAfterDisputeWindowOrResolution(NOW.plusSeconds(7200));
        assertEquals(ReservationStatus.COMPLETED, r.status());
    }
}
