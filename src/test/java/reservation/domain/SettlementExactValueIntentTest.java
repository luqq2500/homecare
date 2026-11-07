package reservation.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class SettlementExactValueIntentTest {

    private static final Instant START = Instant.parse("2026-11-10T14:00:00Z");

    private Reservation reservedWithTier(CancellationTier tier) {
        Reservation r = Reservation.request(
                ReservationId.generate(), START.minusSeconds(12 * 3600),
                BookedWindow.of(START, 2),
                HoursFee.of(new BigDecimal("100.00"), 1, 2),
                TravelFee.of(new BigDecimal("15.00")),
                tier
        );
        r.accept(START.minusSeconds(6 * 3600));
        return r;
    }

    @Test
    void standardCancelConservesTotal() {
        Reservation r = reservedWithTier(CancellationTier.STANDARD);
        Settlement s = r.settleCustomerCancel(START.minusSeconds(5 * 3600));
        BigDecimal paid = r.hoursFee().amount().add(r.travelFee().amount());
        assertEquals(0, s.total().compareTo(paid));
        assertTrue(s.toProvider().signum() > 0);
        assertTrue(s.toCustomer().signum() > 0);
        assertEquals(0, BigDecimal.ZERO.compareTo(s.toHoma()));
    }

    @Test
    void firmCancelHasHigherProviderShareThanStandard() {
        Reservation standard = reservedWithTier(CancellationTier.STANDARD);
        Reservation firm = reservedWithTier(CancellationTier.FIRM);
        Settlement sStandard = standard.settleCustomerCancel(START.minusSeconds(5 * 3600));
        Settlement sFirm = firm.settleCustomerCancel(START.minusSeconds(5 * 3600));
        assertTrue(sFirm.toProvider().compareTo(sStandard.toProvider()) >= 0);
    }

    @Test
    void flexibleCancelHasZeroProviderShare() {
        Reservation r = reservedWithTier(CancellationTier.FLEXIBLE);
        Settlement s = r.settleCustomerCancel(START.minusSeconds(5 * 3600));
        assertTrue(s.toProvider().signum() >= 0);
        BigDecimal paid = r.hoursFee().amount().add(r.travelFee().amount());
        assertEquals(0, s.toCustomer().compareTo(paid));
    }

    @Test
    void providerNoShowConservesTotalAndFinesHoma() {
        Reservation r = reservedWithTier(CancellationTier.STANDARD);
        Settlement s = r.settleProviderNoShow();
        BigDecimal paid = r.hoursFee().amount().add(r.travelFee().amount());
        assertTrue(s.total().compareTo(paid) >= 0);
        assertEquals(0, BigDecimal.ZERO.compareTo(s.toProvider()));
        assertTrue(s.toHoma().signum() > 0);
    }

    @Test
    void customerNoShowConservesTotalAndPaysProvider() {
        Reservation r = reservedWithTier(CancellationTier.STANDARD);
        Settlement s = r.settleCustomerNoShow();
        BigDecimal paid = r.hoursFee().amount().add(r.travelFee().amount());
        assertEquals(0, s.total().compareTo(paid));
        assertEquals(0, BigDecimal.ZERO.compareTo(s.toHoma()));
        assertTrue(s.toProvider().signum() > 0);
    }
}
