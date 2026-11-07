package payment.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class PaymentLifecycleIntentTest {

    private static final Instant NOW = Instant.parse("2026-11-09T10:00:00Z");

    @Test
    void authoriseSetsStatusAuthorised() {
        Payment payment = Payment.authorise(PaymentId.generate(), "res-1", new BigDecimal("130.00"));
        assertEquals(PaymentStatus.AUTHORISED, payment.status());
    }

    @Test
    void holdFromAuthorisedTransitionsToHeld() {
        Payment payment = Payment.authorise(PaymentId.generate(), "res-1", new BigDecimal("130.00"));
        payment.hold(NOW);
        assertEquals(PaymentStatus.HELD, payment.status());
    }

    @Test
    void releaseFromHeldTransitionsToReleased() {
        Payment payment = Payment.authorise(PaymentId.generate(), "res-1", new BigDecimal("130.00"));
        payment.hold(NOW);
        payment.release(NOW.plusSeconds(60));
        assertEquals(PaymentStatus.RELEASED, payment.status());
    }

    @Test
    void refundFromHeldTransitionsToRefunded() {
        Payment payment = Payment.authorise(PaymentId.generate(), "res-1", new BigDecimal("130.00"));
        payment.hold(NOW);
        payment.refund(new BigDecimal("130.00"), NOW.plusSeconds(60));
        assertEquals(PaymentStatus.REFUNDED, payment.status());
    }
}
