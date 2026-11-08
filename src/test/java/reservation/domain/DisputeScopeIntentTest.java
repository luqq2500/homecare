package reservation.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class DisputeScopeIntentTest {

    private static final Instant START = Instant.parse("2026-11-10T14:00:00Z");

    @Test
    void disputeWindowExtendsWhenCompletionIsLate() {
        Reservation r = Reservation.request(
                ReservationId.generate(), START.minusSeconds(3600),
                BookedWindow.of(START, 2),
                HoursFee.of(new BigDecimal("100.00"), 1, 2),
                TravelFee.of(new BigDecimal("15.00")),
                CancellationTier.STANDARD
        );
        r.accept(START.minusSeconds(1800));
        Instant lateCompletion = START.plusSeconds(2 * 3600 + 30 * 60);
        r.markJobCompletedWithEvidence("media-1", lateCompletion);
        Instant windowEnd = r.disputeWindowEnd();
        assertTrue(windowEnd.isAfter(START.plusSeconds(2 * 3600 + 3600)));
    }

    @Test
    void disputeOutsideWindowRejected() {
        Reservation r = Reservation.request(
                ReservationId.generate(), START.minusSeconds(3600),
                BookedWindow.of(START, 2),
                HoursFee.of(new BigDecimal("100.00"), 1, 2),
                TravelFee.of(new BigDecimal("15.00")),
                CancellationTier.STANDARD
        );
        r.accept(START.minusSeconds(1800));
        r.markJobCompletedWithEvidence("media-1", START.plusSeconds(120));
        Instant after = r.disputeWindowEnd().plusSeconds(3600);
        assertThrows(Exception.class,
                () -> r.fileDisputeWithWindow(DisputeType.PROBLEM_WITH_FINISHED_WORK, "late", after));
    }
}
