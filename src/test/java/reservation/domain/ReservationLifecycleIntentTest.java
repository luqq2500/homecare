package reservation.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class ReservationLifecycleIntentTest {

    private static final Instant NOW = Instant.parse("2026-11-09T10:00:00Z");
    private static final Instant START = Instant.parse("2026-11-10T14:00:00Z");

    private Reservation newRequested() {
        return Reservation.request(
                ReservationId.generate(), NOW,
                BookedWindow.of(START, 2),
                HoursFee.of(new BigDecimal("60.00"), 2, 2),
                TravelFee.of(new BigDecimal("10.00")),
                CancellationTier.STANDARD
        );
    }

    @Test
    void requestSetsStatusRequested() {
        Reservation r = newRequested();
        assertEquals(ReservationStatus.REQUESTED, r.status());
    }

    @Test
    void acceptBeforeExpiryTransitionsToReserved() {
        Reservation r = newRequested();
        r.accept(NOW.plusSeconds(60));
        assertEquals(ReservationStatus.RESERVED, r.status());
    }

    @Test
    void declineFromRequestedTransitionsToDeclined() {
        Reservation r = newRequested();
        r.decline();
        assertEquals(ReservationStatus.DECLINED, r.status());
    }

    @Test
    void expireAfterWindowTransitionsToExpired() {
        Reservation r = newRequested();
        r.expire(NOW.plusSeconds(24 * 3600 + 1));
        assertEquals(ReservationStatus.EXPIRED, r.status());
    }
}
