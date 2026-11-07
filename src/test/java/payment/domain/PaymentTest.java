package payment.domain;

import payment.domain.exception.IllegalPaymentTransitionException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class PaymentTest {

    private static final Instant NOW = Instant.parse("2026-11-07T12:00:00Z");

    private Payment newAuthorisedPayment() {
        return Payment.authorise(PaymentId.generate(), "res-1", new BigDecimal("100.00"));
    }

    @Test
    void authoriseCreatesPaymentInAuthorisedStatus() {
        Payment payment = newAuthorisedPayment();
        assertEquals(PaymentStatus.AUTHORISED, payment.status());
        assertEquals(0, new BigDecimal("100.00").compareTo(payment.authorisedAmount()));
        assertEquals(0, BigDecimal.ZERO.compareTo(payment.refundedAmount()));
    }

    @Test
    void authoriseWithZeroOrNegativeAmountIsRejected() {
        assertThrows(IllegalPaymentTransitionException.class,
                () -> Payment.authorise(PaymentId.generate(), "res-1", BigDecimal.ZERO));
        assertThrows(IllegalPaymentTransitionException.class,
                () -> Payment.authorise(PaymentId.generate(), "res-1", new BigDecimal("-1.00")));
    }

    @Test
    void holdFromAuthorisedTransitionsToHeld() {
        Payment payment = newAuthorisedPayment();
        payment.hold(NOW);
        assertEquals(PaymentStatus.HELD, payment.status());
    }

    @Test
    void holdFromNonAuthorisedIsRejected() {
        Payment payment = newAuthorisedPayment();
        payment.hold(NOW);
        assertThrows(IllegalPaymentTransitionException.class, () -> payment.hold(NOW));
    }

    @Test
    void releaseFromHeldTransitionsToReleased() {
        Payment payment = newAuthorisedPayment();
        payment.hold(NOW);
        payment.release(NOW.plusSeconds(60));
        assertEquals(PaymentStatus.RELEASED, payment.status());
    }

    @Test
    void releaseFromNonHeldIsRejected() {
        Payment payment = newAuthorisedPayment();
        assertThrows(IllegalPaymentTransitionException.class, () -> payment.release(NOW));
    }

    @Test
    void partialRefundFromHeldKeepsHeldStatus() {
        Payment payment = newAuthorisedPayment();
        payment.hold(NOW);
        payment.refund(new BigDecimal("40.00"), NOW.plusSeconds(60));
        assertEquals(PaymentStatus.HELD, payment.status());
        assertEquals(0, new BigDecimal("40.00").compareTo(payment.refundedAmount()));
    }

    @Test
    void fullRefundFromHeldTransitionsToRefunded() {
        Payment payment = newAuthorisedPayment();
        payment.hold(NOW);
        payment.refund(new BigDecimal("100.00"), NOW.plusSeconds(60));
        assertEquals(PaymentStatus.REFUNDED, payment.status());
        assertEquals(0, new BigDecimal("100.00").compareTo(payment.refundedAmount()));
    }

    @Test
    void refundFromReleasedIsAllowed() {
        Payment payment = newAuthorisedPayment();
        payment.hold(NOW);
        payment.release(NOW.plusSeconds(60));
        payment.refund(new BigDecimal("30.00"), NOW.plusSeconds(120));
        assertEquals(PaymentStatus.RELEASED, payment.status());
        assertEquals(0, new BigDecimal("30.00").compareTo(payment.refundedAmount()));
    }

    @Test
    void refundExceedingRemainingAmountIsRejected() {
        Payment payment = newAuthorisedPayment();
        payment.hold(NOW);
        payment.refund(new BigDecimal("60.00"), NOW);
        assertThrows(IllegalPaymentTransitionException.class,
                () -> payment.refund(new BigDecimal("50.00"), NOW.plusSeconds(60)));
    }

    @Test
    void refundZeroOrNegativeAmountIsRejected() {
        Payment payment = newAuthorisedPayment();
        payment.hold(NOW);
        assertThrows(IllegalPaymentTransitionException.class,
                () -> payment.refund(BigDecimal.ZERO, NOW));
        assertThrows(IllegalPaymentTransitionException.class,
                () -> payment.refund(new BigDecimal("-10.00"), NOW));
    }

    @Test
    void refundFromAuthorisedIsRejected() {
        Payment payment = newAuthorisedPayment();
        assertThrows(IllegalPaymentTransitionException.class,
                () -> payment.refund(new BigDecimal("10.00"), NOW));
    }

    @Test
    void multiplePartialRefundsAccumulateUntilFull() {
        Payment payment = newAuthorisedPayment();
        payment.hold(NOW);
        payment.refund(new BigDecimal("30.00"), NOW);
        payment.refund(new BigDecimal("40.00"), NOW.plusSeconds(60));
        assertEquals(PaymentStatus.HELD, payment.status());
        assertEquals(0, new BigDecimal("70.00").compareTo(payment.refundedAmount()));

        payment.refund(new BigDecimal("30.00"), NOW.plusSeconds(120));
        assertEquals(PaymentStatus.REFUNDED, payment.status());
    }
}
