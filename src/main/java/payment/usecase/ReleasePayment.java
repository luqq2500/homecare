package payment.usecase;

import payment.domain.Payment;
import payment.domain.PaymentId;
import payment.usecase.dto.ReleasePaymentRequest;
import payment.usecase.dto.ReleasePaymentResponse;
import payment.usecase.exception.PaymentNotFoundException;
import payment.usecase.port.in.PaymentReleaser;
import payment.usecase.port.out.PaymentRepository;
import payment.usecase.port.out.PaymentGateway;
import payment.usecase.port.out.PaymentIEPublisher;
import ie.PaymentReleasedIE;

import java.time.Instant;

public class ReleasePayment implements PaymentReleaser {

    private final PaymentRepository repository;
    private final PaymentGateway gateway;
    private final PaymentIEPublisher publisher;

    public ReleasePayment(PaymentRepository repository, PaymentGateway gateway, PaymentIEPublisher publisher) {
        this.repository = repository;
        this.gateway = gateway;
        this.publisher = publisher;
    }

    @Override
    public ReleasePaymentResponse release(ReleasePaymentRequest request) {
        PaymentId id = PaymentId.of(request.paymentId());
        Payment payment = repository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found: " + id.value()));
        Instant now = Instant.now();
        payment.release(now);
        gateway.release(id.value());
        repository.save(payment);
        publisher.publish(new PaymentReleasedIE(id.value(), now));
        return new ReleasePaymentResponse(id.value(), payment.status().name());
    }
}
