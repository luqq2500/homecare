package payment.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PaymentLifecycleIntentTest {

    @Test
    void authoriseSetsStatusAuthorised() {
        Payment payment = Payment.authorise(PaymentId.generate(), new BigDecimal("130.00"));
        assertEquals(PaymentStatus.AUTHORISED, payment.status());
    }

    @Test
    void holdFromAuthorisedTransitionsToHeld() {
        Payment payment = Payment.authorise(PaymentId.generate(), new BigDecimal("130.00"));
        payment.hold();
        assertEquals(PaymentStatus.HELD, payment.status());
    }

    @Test
    void releaseFromAuthorisedTransitionsToReleased() {
        Payment payment = Payment.authorise(PaymentId.generate(), new BigDecimal("130.00"));
        payment.release();
        assertEquals(PaymentStatus.RELEASED, payment.status());
    }

    @Test
    void refundFromHeldTransitionsToRefunded() {
        Payment payment = Payment.authorise(PaymentId.generate(), new BigDecimal("130.00"));
        payment.hold();
        payment.refund(new BigDecimal("130.00"));
        assertEquals(PaymentStatus.REFUNDED, payment.status());
    }
}
