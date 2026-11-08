package reservation.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class ReservationBoundaryIntentTest {

    private static final Instant START = Instant.parse("2026-11-10T14:00:00Z");

    private Reservation reserved() {
        Reservation r = Reservation.request(
                ReservationId.generate(), START.minusSeconds(12 * 3600),
                BookedWindow.of(START, 2),
                HoursFee.of(new BigDecimal("100.00"), 1, 2),
                TravelFee.of(new BigDecimal("15.00")),
                CancellationTier.STANDARD
        );
        r.accept(START.minusSeconds(6 * 3600));
        return r;
    }

    @Test
    void exactlyTwentyFourHoursBeforeStartIsFree() {
        Reservation r = reserved();
        Instant exactly24h = START.minusSeconds(24 * 3600);
        assertEquals(0, BigDecimal.ZERO.compareTo(r.customerCancellationFee(exactly24h)));
    }

    @Test
    void oneSecondInsideFeeWindowCharges() {
        Reservation r = reserved();
        Instant justInside = START.minusSeconds(24 * 3600 - 1);
        assertTrue(r.customerCancellationFee(justInside).signum() > 0);
    }

    @Test
    void checkInBeforeStartRejected() {
        Reservation r = reserved();
        assertThrows(Exception.class,
                () -> r.recordCheckIn(START.minusSeconds(60),
                        GeoLocation.of(3.1579, 101.7123),
                        GeoLocation.of(3.1580, 101.7124)));
    }

    @Test
    void completionBeforeStartRejected() {
        Reservation r = reserved();
        assertThrows(Exception.class,
                () -> r.markJobCompletedWithEvidence("media-1", START.minusSeconds(60)));
    }

    @Test
    void disputeAfterWindowRejected() {
        Reservation r = reserved();
        r.markJobCompletedWithEvidence("media-1", START.plusSeconds(120));
        Instant afterWindow = r.disputeWindowEnd().plusSeconds(3600);
        assertThrows(Exception.class,
                () -> r.fileDisputeWithWindow(DisputeType.PROBLEM_WITH_FINISHED_WORK, "late", afterWindow));
    }
}
