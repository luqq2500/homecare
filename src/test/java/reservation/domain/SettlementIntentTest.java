package reservation.domain;

import org.junit.jupiter.api.Test;
import payment.domain.Settlement;
import payment.domain.SettlementCalculator;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class SettlementIntentTest {

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
    void settleCustomerCancelReturnsProviderAndCustomerShare() {
        Reservation r = reserved();
        Settlement s = SettlementCalculator.customerCancel(
                r.hoursFee().amount(), r.travelFee().amount(),
                r.cancellationTier().feePercent(), false);
        assertTrue(s.toProvider().signum() >= 0);
        assertTrue(s.toCustomer().signum() >= 0);
        assertEquals(0, s.total().compareTo(r.hoursFee().amount().add(r.travelFee().amount())));
    }

    @Test
    void settleProviderNoShowReturnsFullRefundAndFine() {
        Reservation r = reserved();
        Settlement s = SettlementCalculator.providerNoShow(r.hoursFee().amount(), r.travelFee().amount());
        assertEquals(0, BigDecimal.ZERO.compareTo(s.toProvider()));
        assertTrue(s.toCustomer().signum() > 0);
        assertTrue(s.toHoma().signum() > 0);
    }

    @Test
    void settleCustomerNoShowReturnsCompensationAndRefund() {
        Reservation r = reserved();
        Settlement s = SettlementCalculator.customerNoShow(r.hoursFee().amount(), r.travelFee().amount());
        assertTrue(s.toProvider().signum() > 0);
        assertTrue(s.toCustomer().signum() >= 0);
        assertEquals(0, BigDecimal.ZERO.compareTo(s.toHoma()));
    }

    @Test
    void disputeWindowEndIsLaterOfBookedEndOrCompletion() {
        Reservation r = reserved();
        Instant bookedEnd = START.plusSeconds(2 * 3600);
        assertEquals(bookedEnd.plusSeconds(3600), r.disputeWindowEnd());
    }
}
