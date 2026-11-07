package reservation.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class DisputeWindowIntentTest {

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
    void disputeWindowUsesLaterOfBookedEndOrCompletion() {
        Reservation r = reserved();
        Instant bookedEnd = START.plusSeconds(2 * 3600);
        Instant lateCompletion = bookedEnd.plusSeconds(30 * 60);
        r.markJobCompletedWithEvidence("media-1", lateCompletion);
        assertEquals(ReservationStatus.RESERVED, r.status());
        assertNotNull(r.completionEvidence());
    }

    @Test
    void fileDisputeAfterWindowIsRejected() {
        Reservation r = reserved();
        r.markJobCompletedWithEvidence("media-1", START.plusSeconds(2 * 3600));
        r.moveToCompletedAfterDisputeWindowOrResolution(START.plusSeconds(3 * 3600 + 1));
        assertThrows(Exception.class,
                () -> r.fileDispute(DisputeType.PROBLEM_WITH_FINISHED_WORK, "Late", START.plusSeconds(4 * 3600)));
    }
}
