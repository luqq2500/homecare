package reservation.domain;

import org.junit.jupiter.api.Test;
import reservation.domain.exception.CheckInNotAllowedException;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class CheckInAndNoShowIntentTest {

    private static final Instant START = Instant.parse("2026-11-10T10:00:00Z");

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
    void successfulCheckInWithinRadiusAndPeriod() {
        Reservation r = reserved();
        GeoLocation customer = GeoLocation.of(3.1579, 101.7123);
        GeoLocation provider = GeoLocation.of(3.1580, 101.7124);
        r.recordCheckIn(START.plusSeconds(10 * 60), provider, customer);
        assertTrue(r.hasCheckedIn());
    }

    @Test
    void checkInRejectedWhenOutsideRadius() {
        Reservation r = reserved();
        GeoLocation customer = GeoLocation.of(0, 0);
        GeoLocation provider = GeoLocation.of(0, 1);
        assertThrows(CheckInNotAllowedException.class,
                () -> r.recordCheckIn(START.plusSeconds(5 * 60), provider, customer));
    }

    @Test
    void providerNoShowFineIsThirtyPercent() {
        Reservation r = reserved();
        BigDecimal fine = r.providerNoShowFine();
        assertEquals(0, new BigDecimal("30.00").compareTo(fine));
    }

    @Test
    void customerNoShowCompensationIsTravelPlusThirtyPercentHours() {
        Reservation r = reserved();
        BigDecimal compensation = r.customerNoShowCompensation();
        assertEquals(0, new BigDecimal("45.00").compareTo(compensation));
    }
}
