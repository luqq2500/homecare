package payment.usecase;

import payment.domain.Payment;
import payment.domain.PaymentId;
import payment.usecase.dto.RefundPaymentRequest;
import payment.usecase.dto.RefundPaymentResponse;
import payment.usecase.exception.PaymentNotFoundException;
import payment.usecase.port.in.PaymentRefunder;
import payment.usecase.port.out.PaymentGateway;
import payment.usecase.port.out.PaymentIEPublisher;
import payment.usecase.port.out.PaymentRepository;

import java.time.Instant;

public class RefundPayment implements PaymentRefunder {

    private final PaymentRepository repository;
    private final PaymentIEPublisher publisher;
    private final PaymentGateway gateway;

    public RefundPayment(PaymentRepository repository, PaymentIEPublisher publisher, PaymentGateway gateway) {
        this.repository = repository;
        this.publisher = publisher;
        this.gateway = gateway;
    }

    @Override
    public RefundPaymentResponse refund(RefundPaymentRequest request) {
        PaymentId id = PaymentId.of(request.paymentId());
        Payment payment = repository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found: " + id.value()));
        gateway.refund(id.value(), request.amount());
        payment.refund(request.amount(), Instant.now());
        repository.save(payment);
        publisher.publishRefunded(id);
        return new RefundPaymentResponse(id.value(), payment.status().name(), payment.refundedAmount().toPlainString());
    }
}
