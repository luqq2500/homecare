package payment.domain;

import payment.domain.exception.IllegalPaymentTransitionException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * Aggregate root for a Homa payment.
 * Orthogonal state machine: Authorised → Held → Released / Refunded.
 */
public class Payment {

    private final PaymentId id;
    private final String reservationId;
    private final BigDecimal authorisedAmount;
    private PaymentStatus status;
    private Instant heldAt;
    private Instant releasedAt;
    private Instant refundedAt;
    private BigDecimal refundedAmount;

    private Payment(PaymentId id, String reservationId, BigDecimal authorisedAmount) {
        this.id = Objects.requireNonNull(id);
        this.reservationId = Objects.requireNonNull(reservationId);
        this.authorisedAmount = Objects.requireNonNull(authorisedAmount);
        this.status = PaymentStatus.AUTHORISED;
        this.refundedAmount = BigDecimal.ZERO;
    }

    public static Payment authorise(PaymentId id, String reservationId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalPaymentTransitionException("Authorised amount must be positive");
        }
        return new Payment(id, reservationId, amount);
    }

    public void hold(Instant now) {
        if (status != PaymentStatus.AUTHORISED) {
            throw new IllegalPaymentTransitionException("Can only hold from AUTHORISED");
        }
        this.status = PaymentStatus.HELD;
        this.heldAt = now;
    }

    public void release(Instant now) {
        if (status != PaymentStatus.HELD) {
            throw new IllegalPaymentTransitionException("Can only release from HELD");
        }
        this.status = PaymentStatus.RELEASED;
        this.releasedAt = now;
    }

    public void refund(BigDecimal amount, Instant now) {
        if (status != PaymentStatus.HELD && status != PaymentStatus.RELEASED) {
            throw new IllegalPaymentTransitionException("Can only refund from HELD or RELEASED");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalPaymentTransitionException("Refund amount must be positive");
        }
        BigDecimal remaining = authorisedAmount.subtract(refundedAmount);
        if (amount.compareTo(remaining) > 0) {
            throw new IllegalPaymentTransitionException("Refund exceeds remaining amount");
        }
        this.refundedAmount = this.refundedAmount.add(amount);
        this.refundedAt = now;
        if (this.refundedAmount.compareTo(authorisedAmount) == 0) {
            this.status = PaymentStatus.REFUNDED;
        }
    }

    public PaymentId id() { return id; }
    public String reservationId() { return reservationId; }
    public BigDecimal authorisedAmount() { return authorisedAmount; }
    public PaymentStatus status() { return status; }
    public BigDecimal refundedAmount() { return refundedAmount; }
}
