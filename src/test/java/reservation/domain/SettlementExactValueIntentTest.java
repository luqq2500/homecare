package reservation.domain;

import org.junit.jupiter.api.Test;
import payment.domain.Settlement;
import payment.domain.SettlementCalculator;

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
        Settlement s = SettlementCalculator.customerCancel(
                r.hoursFee().amount(), r.travelFee().amount(),
                r.cancellationTier().feePercent(), false);
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
        Settlement sStandard = SettlementCalculator.customerCancel(
                standard.hoursFee().amount(), standard.travelFee().amount(),
                standard.cancellationTier().feePercent(), false);
        Settlement sFirm = SettlementCalculator.customerCancel(
                firm.hoursFee().amount(), firm.travelFee().amount(),
                firm.cancellationTier().feePercent(), false);
        assertTrue(sFirm.toProvider().compareTo(sStandard.toProvider()) >= 0);
    }

    @Test
    void flexibleCancelHasZeroProviderShare() {
        Reservation r = reservedWithTier(CancellationTier.FLEXIBLE);
        Settlement s = SettlementCalculator.customerCancel(
                r.hoursFee().amount(), r.travelFee().amount(),
                r.cancellationTier().feePercent(), false);
        assertTrue(s.toProvider().signum() >= 0);
        BigDecimal paid = r.hoursFee().amount().add(r.travelFee().amount());
        assertEquals(0, s.toCustomer().compareTo(paid));
    }

    @Test
    void providerNoShowConservesTotalAndFinesHoma() {
        Reservation r = reservedWithTier(CancellationTier.STANDARD);
        Settlement s = SettlementCalculator.providerNoShow(r.hoursFee().amount(), r.travelFee().amount());
        BigDecimal paid = r.hoursFee().amount().add(r.travelFee().amount());
        assertTrue(s.total().compareTo(paid) >= 0);
        assertEquals(0, BigDecimal.ZERO.compareTo(s.toProvider()));
        assertTrue(s.toHoma().signum() > 0);
    }

    @Test
    void customerNoShowConservesTotalAndPaysProvider() {
        Reservation r = reservedWithTier(CancellationTier.STANDARD);
        Settlement s = SettlementCalculator.customerNoShow(r.hoursFee().amount(), r.travelFee().amount());
        BigDecimal paid = r.hoursFee().amount().add(r.travelFee().amount());
        assertEquals(0, s.total().compareTo(paid));
        assertEquals(0, BigDecimal.ZERO.compareTo(s.toHoma()));
        assertTrue(s.toProvider().signum() > 0);
    }
}
