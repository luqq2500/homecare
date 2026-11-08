package payment.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class PaymentMutationIntentTest {

    private static final Instant NOW = Instant.parse("2026-11-10T14:00:00Z");

    @Test
    void voidFromAuthorisedTransitionsToVoided() {
        Payment payment = Payment.authorise(PaymentId.generate(), "res-1", new BigDecimal("100.00"));
        payment.voidAuthorisation(NOW);
        assertEquals(PaymentStatus.VOIDED, payment.status());
    }

    @Test
    void voidFromHeldRejected() {
        Payment payment = Payment.authorise(PaymentId.generate(), "res-1", new BigDecimal("100.00"));
        payment.hold(NOW);
        assertThrows(Exception.class, () -> payment.voidAuthorisation(NOW.plusSeconds(60)));
    }

    @Test
    void releasableAmountIsAuthorisedMinusRefunded() {
        Payment payment = Payment.authorise(PaymentId.generate(), "res-1", new BigDecimal("100.00"));
        payment.hold(NOW);
        payment.refund(new BigDecimal("30.00"), NOW.plusSeconds(60));
        assertEquals(0, new BigDecimal("70.00").compareTo(payment.releasableAmount()));
    }

    @Test
    void settleFromHeldTransitionsToSettled() {
        Payment payment = Payment.authorise(PaymentId.generate(), "res-1", new BigDecimal("100.00"));
        payment.hold(NOW);
        payment.settle(new BigDecimal("20.00"), new BigDecimal("80.00"), NOW.plusSeconds(60));
        assertTrue(payment.status() == PaymentStatus.RELEASED || payment.status() == PaymentStatus.REFUNDED);
    }

    @Test
    void settleFromAuthorisedRejected() {
        Payment payment = Payment.authorise(PaymentId.generate(), "res-1", new BigDecimal("100.00"));
        assertThrows(Exception.class,
                () -> payment.settle(new BigDecimal("20.00"), new BigDecimal("80.00"), NOW));
    }

    @Test
    void refundExceedingAuthorisedRejected() {
        Payment payment = Payment.authorise(PaymentId.generate(), "res-1", new BigDecimal("100.00"));
        payment.hold(NOW);
        assertThrows(Exception.class,
                () -> payment.refund(new BigDecimal("150.00"), NOW.plusSeconds(60)));
    }

    @Test
    void releaseFromHeldTransitionsToReleased() {
        Payment payment = Payment.authorise(PaymentId.generate(), "res-1", new BigDecimal("100.00"));
        payment.hold(NOW);
        payment.release(NOW.plusSeconds(60));
        assertEquals(PaymentStatus.RELEASED, payment.status());
    }

    @Test
    void releaseFromAuthorisedRejected() {
        Payment payment = Payment.authorise(PaymentId.generate(), "res-1", new BigDecimal("100.00"));
        assertThrows(Exception.class, () -> payment.release(NOW));
    }
}
