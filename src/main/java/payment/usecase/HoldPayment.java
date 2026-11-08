package payment.usecase;

import payment.domain.Payment;
import payment.domain.PaymentId;
import payment.usecase.dto.HoldPaymentRequest;
import payment.usecase.dto.HoldPaymentResponse;
import payment.usecase.exception.PaymentNotFoundException;
import payment.usecase.port.in.PaymentHolder;
import payment.usecase.port.out.PaymentRepository;
import payment.usecase.port.out.PaymentGateway;
import payment.usecase.port.out.PaymentIEPublisher;
import ie.PaymentHeldIE;

import java.time.Instant;

public class HoldPayment implements PaymentHolder {

    private final PaymentRepository repository;
    private final PaymentGateway gateway;
    private final PaymentIEPublisher publisher;

    public HoldPayment(PaymentRepository repository, PaymentGateway gateway, PaymentIEPublisher publisher) {
        this.repository = repository;
        this.gateway = gateway;
        this.publisher = publisher;
    }

    @Override
    public HoldPaymentResponse hold(HoldPaymentRequest request) {
        PaymentId id = PaymentId.of(request.paymentId());
        Payment payment = repository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found: " + id.value()));
        Instant now = Instant.now();
        payment.hold(now);
        gateway.capture(id.value());
        repository.save(payment);
        publisher.publish(new PaymentHeldIE(id.value(), now));
        return new HoldPaymentResponse(id.value(), payment.status().name());
    }
}
