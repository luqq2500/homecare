package payment.usecase;

import payment.domain.Payment;
import payment.domain.PaymentId;
import payment.usecase.dto.HoldPaymentRequest;
import payment.usecase.dto.HoldPaymentResponse;
import payment.usecase.exception.PaymentNotFoundException;
import payment.usecase.port.in.PaymentHolder;
import payment.usecase.port.out.PaymentGateway;
import payment.usecase.port.out.PaymentIEPublisher;
import payment.usecase.port.out.PaymentRepository;

import java.time.Instant;

public class HoldPayment implements PaymentHolder {

    private final PaymentRepository repository;
    private final PaymentIEPublisher publisher;
    private final PaymentGateway gateway;

    public HoldPayment(PaymentRepository repository, PaymentIEPublisher publisher, PaymentGateway gateway) {
        this.repository = repository;
        this.publisher = publisher;
        this.gateway = gateway;
    }

    @Override
    public HoldPaymentResponse hold(HoldPaymentRequest request) {
        PaymentId id = PaymentId.of(request.paymentId());
        Payment payment = repository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found: " + id.value()));
        gateway.capture(id.value());
        payment.hold(Instant.now());
        repository.save(payment);
        publisher.publishHeld(id);
        return new HoldPaymentResponse(id.value(), payment.status().name());
    }
}
