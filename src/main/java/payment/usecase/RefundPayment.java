package payment.usecase;

import payment.domain.Payment;
import payment.domain.PaymentId;
import payment.usecase.dto.RefundPaymentRequest;
import payment.usecase.dto.RefundPaymentResponse;
import payment.usecase.exception.PaymentNotFoundException;
import payment.usecase.port.in.PaymentRefunder;
import payment.usecase.port.out.PaymentRepository;
import payment.usecase.port.out.PaymentGateway;
import payment.usecase.port.out.PaymentIEPublisher;
import ie.PaymentRefundedIE;

import java.math.BigDecimal;
import java.time.Instant;

public class RefundPayment implements PaymentRefunder {

    private final PaymentRepository repository;
    private final PaymentGateway gateway;
    private final PaymentIEPublisher publisher;

    public RefundPayment(PaymentRepository repository, PaymentGateway gateway, PaymentIEPublisher publisher) {
        this.repository = repository;
        this.gateway = gateway;
        this.publisher = publisher;
    }

    @Override
    public RefundPaymentResponse refund(RefundPaymentRequest request) {
        PaymentId id = PaymentId.of(request.paymentId());
        Payment payment = repository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found: " + id.value()));
        BigDecimal amount = request.amount() == null ? payment.authorisedAmount() : request.amount();
        Instant now = Instant.now();
        payment.refund(amount, now);
        gateway.refund(id.value(), amount);
        repository.save(payment);
        publisher.publish(new PaymentRefundedIE(id.value(), now));
        return new RefundPaymentResponse(id.value(), payment.status().name(), payment.refundedAmount().toPlainString());
    }
}
