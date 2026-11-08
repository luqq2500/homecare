package payment.domain;

import payment.domain.exception.IllegalPaymentTransitionException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class PaymentIntentTargetedTest {

    private static final Instant NOW = Instant.parse("2026-11-09T10:00:00Z");

    @Test
    void settleRejectsWhenTotalDoesNotEqualAuthorised() {
        Payment payment = Payment.authorise(PaymentId.generate(), "res-1", new BigDecimal("130.00"));
        payment.hold(NOW);
        assertThrows(IllegalPaymentTransitionException.class,
                () -> payment.settle(new BigDecimal("20.00"), new BigDecimal("100.00"), NOW));
    }

    @Test
    void settleRejectsFromAuthorised() {
        Payment payment = Payment.authorise(PaymentId.generate(), "res-1", new BigDecimal("130.00"));
        assertThrows(IllegalPaymentTransitionException.class,
                () -> payment.settle(new BigDecimal("20.00"), new BigDecimal("110.00"), NOW));
    }

    @Test
    void voidRejectsFromHeld() {
        Payment payment = Payment.authorise(PaymentId.generate(), "res-1", new BigDecimal("130.00"));
        payment.hold(NOW);
        assertThrows(IllegalPaymentTransitionException.class,
                () -> payment.voidAuthorisation(NOW));
    }

    @Test
    void settleWithZeroProviderShareStillRefunds() {
        Payment payment = Payment.authorise(PaymentId.generate(), "res-1", new BigDecimal("130.00"));
        payment.hold(NOW);
        payment.settle(BigDecimal.ZERO, new BigDecimal("130.00"), NOW);
        assertEquals(PaymentStatus.REFUNDED, payment.status());
        assertEquals(0, new BigDecimal("130.00").compareTo(payment.refundedAmount()));
    }

    @Test
    void calculatorCustomerCancelBeforeStartAppliesTier() {
        Settlement s = SettlementCalculator.customerCancel(
                new BigDecimal("200.00"), new BigDecimal("15.00"), 10, false);
        assertEquals(0, new BigDecimal("20.00").compareTo(s.toProvider()));
        assertEquals(0, new BigDecimal("195.00").compareTo(s.toCustomer()));
        assertEquals(0, BigDecimal.ZERO.compareTo(s.toHoma()));
    }

    @Test
    void calculatorCustomerCancelAfterStartUsesNoShowCompensation() {
        Settlement s = SettlementCalculator.customerCancel(
                new BigDecimal("200.00"), new BigDecimal("15.00"), 10, true);
        assertTrue(s.toProvider().signum() > 0);
        assertEquals(0, BigDecimal.ZERO.compareTo(s.toHoma()));
    }

    @Test
    void calculatorProviderNoShowPutsFineToHoma() {
        Settlement s = SettlementCalculator.providerNoShow(
                new BigDecimal("200.00"), new BigDecimal("15.00"));
        assertEquals(0, BigDecimal.ZERO.compareTo(s.toProvider()));
        assertTrue(s.toHoma().signum() > 0);
    }

    @Test
    void calculatorCustomerNoShowCompensatesProvider() {
        Settlement s = SettlementCalculator.customerNoShow(
                new BigDecimal("200.00"), new BigDecimal("15.00"));
        assertTrue(s.toProvider().signum() > 0);
        assertEquals(0, BigDecimal.ZERO.compareTo(s.toHoma()));
    }

    @Test
    void settlementTotalConserves() {
        Settlement s = Settlement.of(new BigDecimal("20.00"), new BigDecimal("195.00"), BigDecimal.ZERO);
        assertEquals(0, new BigDecimal("215.00").compareTo(s.total()));
    }
}
