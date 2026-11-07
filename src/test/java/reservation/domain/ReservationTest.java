package reservation.domain;

import reservation.domain.exception.InvalidStatusTransitionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class ReservationTest {

    private static final Instant NOW = Instant.parse("2026-11-07T12:00:00Z");
    private static final Instant FUTURE_START = Instant.parse("2026-11-08T14:00:00Z"); // on 15-min boundary

    private Reservation newRequestedReservation(CancellationTier tier) {
        return Reservation.request(
                ReservationId.generate(),
                NOW,
                BookedWindow.of(FUTURE_START, 2),
                HoursFee.of(new BigDecimal("100.00"), 1, 2),
                TravelFee.of(new BigDecimal("10.00")),
                tier
        );
    }

    private Reservation newRequestedReservation() {
        return newRequestedReservation(CancellationTier.STANDARD);
    }

    @Test
    void whenCreatedShouldPlaceReservationInRequestedStatus() {
        Reservation reservation = newRequestedReservation();
        assertEquals(ReservationStatus.REQUESTED, reservation.status());
    }

    @Test
    void whenProviderAcceptsBeforeExpiryShouldTransitionToReserved() {
        Reservation reservation = newRequestedReservation();
        reservation.accept(NOW.plusSeconds(60));
        assertEquals(ReservationStatus.RESERVED, reservation.status());
    }

    @Test
    void whenRequestHasAlreadyExpiredShouldRejectAccept() {
        Reservation reservation = newRequestedReservation();
        Instant afterExpiry = FUTURE_START.plusSeconds(1);
        assertThrows(InvalidStatusTransitionException.class, () -> reservation.accept(afterExpiry));
    }

    @Test
    void whenProviderDeclinesRequestShouldTransitionToDeclined() {
        Reservation reservation = newRequestedReservation();
        reservation.decline();
        assertEquals(ReservationStatus.DECLINED, reservation.status());
    }

    @Test
    void whenExpiryTimeIsReachedShouldTransitionToExpired() {
        Reservation reservation = newRequestedReservation();
        Instant afterExpiry = FUTURE_START.plusSeconds(1);
        reservation.expire(afterExpiry);
        assertEquals(ReservationStatus.EXPIRED, reservation.status());
    }

    @Test
    void whenRequestHasNotYetExpiredShouldRejectExpire() {
        Reservation reservation = newRequestedReservation();
        assertThrows(InvalidStatusTransitionException.class, () -> reservation.expire(NOW.plusSeconds(60)));
    }

    @Test
    void whenCustomerCancelsFromRequestedShouldTransitionToCancelled() {
        Reservation reservation = newRequestedReservation();
        reservation.cancelByCustomer(NOW.plusSeconds(60));
        assertEquals(ReservationStatus.CANCELLED, reservation.status());
    }

    @Test
    void whenCustomerCancelsFromReservedShouldTransitionToCancelled() {
        Reservation reservation = newRequestedReservation();
        reservation.accept(NOW.plusSeconds(60));
        reservation.cancelByCustomer(NOW.plusSeconds(120));
        assertEquals(ReservationStatus.CANCELLED, reservation.status());
    }

    @Test
    void whenProviderCancelsFromReservedShouldTransitionToCancelled() {
        Reservation reservation = newRequestedReservation();
        reservation.accept(NOW.plusSeconds(60));
        reservation.cancelByProvider(NOW.plusSeconds(120));
        assertEquals(ReservationStatus.CANCELLED, reservation.status());
    }

    @Test
    void whenDisputeIsRaisedFromReservedShouldTransitionToDisputed() {
        Reservation reservation = newRequestedReservation();
        reservation.accept(NOW.plusSeconds(60));
        reservation.markDisputed();
        assertEquals(ReservationStatus.DISPUTED, reservation.status());
    }

    @Test
    void whenProviderCompletesFromReservedShouldTransitionToCompleted() {
        Reservation reservation = newRequestedReservation();
        reservation.accept(NOW.plusSeconds(60));
        reservation.complete(NOW.plusSeconds(3600));
        assertEquals(ReservationStatus.COMPLETED, reservation.status());
    }

    @Test
    void whenTerminalStatusIsReachedShouldRejectFurtherTransitions() {
        Reservation reservation = newRequestedReservation();
        reservation.decline();

        assertThrows(InvalidStatusTransitionException.class, () -> reservation.accept(NOW));
        assertThrows(InvalidStatusTransitionException.class, () -> reservation.complete(NOW));
        assertThrows(InvalidStatusTransitionException.class, reservation::markDisputed);
    }

    @Test
    void whenCheckingExpiryShouldUseTheEarlierOfTwentyFourHoursOrBookedStart() {
        Reservation reservation = newRequestedReservation();
        assertFalse(reservation.isExpired(NOW.plusSeconds(3600)));
        assertTrue(reservation.isExpired(FUTURE_START.plusSeconds(1)));
    }

    // --- Edge cases: cancellation fee matrix ---

    @ParameterizedTest
    @CsvSource({
            "FLEXIBLE, false, 0.00",
            "FLEXIBLE, true,  0.00",
            "STANDARD, false, 0.00",
            "STANDARD, true,  20.00",
            "FIRM,     false, 0.00",
            "FIRM,     true,  40.00"
    })
    void whenCalculatingCustomerCancellationFeeShouldChargeOnlyInsideFeeWindowAccordingToTier(
            CancellationTier tier, boolean insideWindow, String expectedFee) {
        Reservation reservation = newRequestedReservation(tier);
        Instant now = insideWindow
                ? FUTURE_START.minusSeconds(12 * 3600)   // inside 24h window
                : FUTURE_START.minusSeconds(36 * 3600);  // outside 24h window

        BigDecimal fee = reservation.customerCancellationFee(now);
        assertEquals(0, new BigDecimal(expectedFee).compareTo(fee));
    }

    @Test
    void whenStatusIsNoLongerCancellableShouldRejectCancellationFeeCalculation() {
        Reservation reservation = newRequestedReservation();
        reservation.decline();
        assertThrows(InvalidStatusTransitionException.class,
                () -> reservation.customerCancellationFee(NOW));
    }

    @Test
    void whenLateCancellationInsideFeeWindowShouldApplyTwentyPercentProviderFine() {
        Reservation reservation = newRequestedReservation();
        Instant inside = FUTURE_START.minusSeconds(12 * 3600);
        BigDecimal fine = reservation.providerCancellationFine(inside);
        assertEquals(0, new BigDecimal("40.00").compareTo(fine)); // 20% of 200
    }

    @Test
    void whenCancellationIsOutsideFeeWindowShouldApplyZeroProviderFine() {
        Reservation reservation = newRequestedReservation();
        Instant outside = FUTURE_START.minusSeconds(36 * 3600);
        BigDecimal fine = reservation.providerCancellationFine(outside);
        assertEquals(0, BigDecimal.ZERO.compareTo(fine));
    }

    @Test
    void whenAssessingProviderNoShowShouldApplyThirtyPercentCeilingAsFine() {
        Reservation reservation = newRequestedReservation();
        BigDecimal fine = reservation.providerNoShowFine();
        assertEquals(0, new BigDecimal("60.00").compareTo(fine)); // 30% of 200
    }

    @Test
    void whenAssessingProviderNoShowFineShouldNeverExceedThirtyPercentOfHoursFee() {
        Reservation reservation = newRequestedReservation();
        BigDecimal fine = reservation.providerNoShowFine();
        BigDecimal ceiling = reservation.hoursFee().amount().multiply(new BigDecimal("0.30"));
        assertTrue(fine.compareTo(ceiling) <= 0);
    }

    // --- Expiry edge cases ---

    @Test
    void whenExactlyAtBookedStartShouldTreatReservationAsExpired() {
        Reservation reservation = newRequestedReservation();
        assertTrue(reservation.isExpired(FUTURE_START));
    }

    @Test
    void whenBookedStartIsEarlierThanTwentyFourHoursAfterRequestShouldExpireAtBookedStart() {
        Instant earlyStart = NOW.plusSeconds(12 * 3600); // start in 12h
        Reservation reservation = Reservation.request(
                ReservationId.generate(),
                NOW,
                BookedWindow.of(earlyStart, 1),
                HoursFee.of(new BigDecimal("50.00"), 1, 1),
                TravelFee.ZERO,
                CancellationTier.FLEXIBLE
        );
        // booked start is earlier, so expiry is at start
        assertTrue(reservation.isExpired(earlyStart));
        assertFalse(reservation.isExpired(earlyStart.minusSeconds(1)));
    }
}
