package reservation.domain;

import org.junit.jupiter.api.Test;
import reservation.domain.exception.CheckInNotAllowedException;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class CheckInTest {

    private static final Instant BOOKED_START = Instant.parse("2026-11-08T14:00:00Z");
    private static final GeoLocation CUSTOMER = GeoLocation.of(3.1579, 101.7123);
    private static final GeoLocation NEARBY = GeoLocation.of(3.1585, 101.7125);
    private static final GeoLocation FAR = GeoLocation.of(3.2000, 101.8000);

    @Test
    void recordCheckInWithinRadiusAndPeriodSucceeds() {
        Instant withinPeriod = BOOKED_START.plusSeconds(5 * 60);
        CheckIn checkIn = CheckIn.record(withinPeriod, BOOKED_START, NEARBY, CUSTOMER);
        assertEquals(withinPeriod, checkIn.checkedInAt());
        assertEquals(NEARBY, checkIn.providerLocation());
    }

    @Test
    void recordCheckInAfterPeriodExpiresIsRejected() {
        Instant tooLate = BOOKED_START.plusSeconds(16 * 60);
        assertThrows(CheckInNotAllowedException.class,
                () -> CheckIn.record(tooLate, BOOKED_START, NEARBY, CUSTOMER));
    }

    @Test
    void recordCheckInOutsideRadiusIsRejected() {
        Instant withinPeriod = BOOKED_START.plusSeconds(60);
        assertThrows(CheckInNotAllowedException.class,
                () -> CheckIn.record(withinPeriod, BOOKED_START, FAR, CUSTOMER));
    }

    @Test
    void reservationRecordCheckInOnlyWhileReserved() {
        Reservation reservation = Reservation.request(
                ReservationId.generate(),
                Instant.parse("2026-11-07T12:00:00Z"),
                BookedWindow.of(BOOKED_START, 2),
                HoursFee.of(java.math.BigDecimal.TEN, 1, 2),
                TravelFee.ZERO,
                CancellationTier.STANDARD
        );
        assertThrows(Exception.class,
                () -> reservation.recordCheckIn(BOOKED_START, NEARBY, CUSTOMER));

        reservation.accept(Instant.parse("2026-11-07T13:00:00Z"));
        reservation.recordCheckIn(BOOKED_START.plusSeconds(60), NEARBY, CUSTOMER);
        assertTrue(reservation.hasCheckedIn());
        assertNotNull(reservation.checkIn());
    }
}
