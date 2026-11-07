package reservation.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NoShowActionIntentTest {

    private static final Instant START = Instant.parse("2026-11-10T14:00:00Z");

    @Test
    void requestWithIdentityStoresCustomerProviderAndJobs() {
        PricingSnapshot snapshot = PricingSnapshot.of(
                new BigDecimal("50.00"), 1, 2, false, new BigDecimal("10.00"), CancellationTier.STANDARD);
        Reservation r = Reservation.requestWithIdentity(
                ReservationId.generate(), START.minusSeconds(3600),
                BookedWindow.of(START, 2),
                HoursFee.of(new BigDecimal("50.00"), 1, 2),
                TravelFee.of(new BigDecimal("10.00")),
                CancellationTier.STANDARD,
                "cust-1", "prov-1", List.of("Kitchen care"), snapshot);
        assertEquals("cust-1", r.customerId());
        assertEquals("prov-1", r.providerId());
        assertEquals(List.of("Kitchen care"), r.jobs());
        assertEquals(snapshot, r.pricingSnapshot());
    }

    @Test
    void reportProviderNoShowCancelsAndReturnsSettlement() {
        Reservation r = Reservation.request(
                ReservationId.generate(), START.minusSeconds(3600),
                BookedWindow.of(START, 2),
                HoursFee.of(new BigDecimal("100.00"), 1, 2),
                TravelFee.of(new BigDecimal("15.00")),
                CancellationTier.STANDARD);
        r.accept(START.minusSeconds(1800));
        Settlement s = r.reportProviderNoShow(START.plusSeconds(16 * 60));
        assertEquals(ReservationStatus.CANCELLED, r.status());
        assertNotNull(r.cancellation());
        assertEquals(0, BigDecimal.ZERO.compareTo(s.toProvider()));
        assertTrue(s.toHoma().signum() > 0);
    }

    @Test
    void reportCustomerNoShowAfterCheckInCancelsAndReturnsSettlement() {
        Reservation r = Reservation.request(
                ReservationId.generate(), START.minusSeconds(3600),
                BookedWindow.of(START, 2),
                HoursFee.of(new BigDecimal("100.00"), 1, 2),
                TravelFee.of(new BigDecimal("15.00")),
                CancellationTier.STANDARD);
        r.accept(START.minusSeconds(1800));
        r.recordCheckIn(START.plusSeconds(5 * 60),
                GeoLocation.of(3.1579, 101.7123),
                GeoLocation.of(3.1580, 101.7124));
        Settlement s = r.reportCustomerNoShow(START.plusSeconds(21 * 60));
        assertEquals(ReservationStatus.CANCELLED, r.status());
        assertNotNull(r.cancellation());
        assertTrue(s.toProvider().signum() > 0);
        assertEquals(0, BigDecimal.ZERO.compareTo(s.toHoma()));
    }
}
