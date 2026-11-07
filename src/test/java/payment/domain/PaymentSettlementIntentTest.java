package payment.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class PaymentSettlementIntentTest {

    private static final Instant NOW = Instant.parse("2026-11-09T10:00:00Z");

    @Test
    void voidFromAuthorisedTransitionsToVoided() {
        Payment payment = Payment.authorise(PaymentId.generate(), "res-1", new BigDecimal("130.00"));
        payment.voidAuthorisation(NOW);
        assertEquals(PaymentStatus.VOIDED, payment.status());
    }

    @Test
    void releasableAmountIsAuthorisedMinusRefunded() {
        Payment payment = Payment.authorise(PaymentId.generate(), "res-1", new BigDecimal("130.00"));
        payment.hold(NOW);
        payment.refund(new BigDecimal("30.00"), NOW.plusSeconds(60));
        assertEquals(0, new BigDecimal("100.00").compareTo(payment.releasableAmount()));
    }

    @Test
    void settleReleasesProviderShareAndRefundsCustomerShare() {
        Payment payment = Payment.authorise(PaymentId.generate(), "res-1", new BigDecimal("130.00"));
        payment.hold(NOW);
        payment.settle(new BigDecimal("20.00"), new BigDecimal("110.00"), NOW.plusSeconds(60));
        assertEquals(PaymentStatus.RELEASED, payment.status());
        assertEquals(0, new BigDecimal("110.00").compareTo(payment.refundedAmount()));
    }

    @Test
    void settleFullRefundTransitionsToRefunded() {
        Payment payment = Payment.authorise(PaymentId.generate(), "res-1", new BigDecimal("130.00"));
        payment.hold(NOW);
        payment.settle(BigDecimal.ZERO, new BigDecimal("130.00"), NOW.plusSeconds(60));
        assertEquals(PaymentStatus.REFUNDED, payment.status());
    }
}
