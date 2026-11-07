package reservation.domain;

import reservation.domain.exception.InvalidStatusTransitionException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class ReservationTest {

    private static final Instant NOW = Instant.parse("2026-11-07T12:00:00Z");
    private static final Instant FUTURE_START = Instant.parse("2026-11-08T14:00:00Z"); // on 15-min boundary

    private Reservation newRequestedReservation() {
        return Reservation.request(
                ReservationId.generate(),
                NOW,
                BookedWindow.of(FUTURE_START, 2),
                HoursFee.of(new BigDecimal("25.00"), 1, 2),
                TravelFee.ZERO,
                CancellationTier.STANDARD
        );
    }

    @Test
    void request_createsReservationInRequestedStatus() {
        Reservation reservation = newRequestedReservation();
        assertEquals(ReservationStatus.REQUESTED, reservation.status());
    }

    @Test
    void accept_fromRequested_movesToReserved() {
        Reservation reservation = newRequestedReservation();
        reservation.accept(NOW.plusSeconds(60));
        assertEquals(ReservationStatus.RESERVED, reservation.status());
    }

    @Test
    void accept_whenExpired_throws() {
        Reservation reservation = newRequestedReservation();
        Instant afterExpiry = FUTURE_START.plusSeconds(1);
        assertThrows(InvalidStatusTransitionException.class, () -> reservation.accept(afterExpiry));
    }

    @Test
    void decline_fromRequested_movesToDeclined() {
        Reservation reservation = newRequestedReservation();
        reservation.decline();
        assertEquals(ReservationStatus.DECLINED, reservation.status());
    }

    @Test
    void expire_whenPastExpiry_movesToExpired() {
        Reservation reservation = newRequestedReservation();
        Instant afterExpiry = FUTURE_START.plusSeconds(1);
        reservation.expire(afterExpiry);
        assertEquals(ReservationStatus.EXPIRED, reservation.status());
    }

    @Test
    void expire_beforeExpiry_throws() {
        Reservation reservation = newRequestedReservation();
        assertThrows(InvalidStatusTransitionException.class, () -> reservation.expire(NOW.plusSeconds(60)));
    }

    @Test
    void cancelByCustomer_fromRequested_movesToCancelled() {
        Reservation reservation = newRequestedReservation();
        reservation.cancelByCustomer(NOW.plusSeconds(60));
        assertEquals(ReservationStatus.CANCELLED, reservation.status());
    }

    @Test
    void cancelByCustomer_fromReserved_movesToCancelled() {
        Reservation reservation = newRequestedReservation();
        reservation.accept(NOW.plusSeconds(60));
        reservation.cancelByCustomer(NOW.plusSeconds(120));
        assertEquals(ReservationStatus.CANCELLED, reservation.status());
    }

    @Test
    void cancelByProvider_fromReserved_movesToCancelled() {
        Reservation reservation = newRequestedReservation();
        reservation.accept(NOW.plusSeconds(60));
        reservation.cancelByProvider(NOW.plusSeconds(120));
        assertEquals(ReservationStatus.CANCELLED, reservation.status());
    }

    @Test
    void markDisputed_fromReserved_movesToDisputed() {
        Reservation reservation = newRequestedReservation();
        reservation.accept(NOW.plusSeconds(60));
        reservation.markDisputed();
        assertEquals(ReservationStatus.DISPUTED, reservation.status());
    }

    @Test
    void complete_fromReserved_movesToCompleted() {
        Reservation reservation = newRequestedReservation();
        reservation.accept(NOW.plusSeconds(60));
        reservation.complete(NOW.plusSeconds(3600));
        assertEquals(ReservationStatus.COMPLETED, reservation.status());
    }

    @Test
    void illegalTransitions_throwInvalidStatusTransition() {
        Reservation reservation = newRequestedReservation();
        reservation.decline();

        assertThrows(InvalidStatusTransitionException.class, () -> reservation.accept(NOW));
        assertThrows(InvalidStatusTransitionException.class, () -> reservation.complete(NOW));
        assertThrows(InvalidStatusTransitionException.class, reservation::markDisputed);
    }

    @Test
    void isExpired_respectsEarlierOf24hOrBookedStart() {
        Reservation reservation = newRequestedReservation();
        // Before both
        assertFalse(reservation.isExpired(NOW.plusSeconds(3600)));
        // After booked start (which is earlier than 24h)
        assertTrue(reservation.isExpired(FUTURE_START.plusSeconds(1)));
    }
}
