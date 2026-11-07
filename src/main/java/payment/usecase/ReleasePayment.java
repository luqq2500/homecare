package payment.usecase;

import payment.domain.Payment;
import payment.domain.PaymentId;
import payment.usecase.dto.ReleasePaymentRequest;
import payment.usecase.dto.ReleasePaymentResponse;
import payment.usecase.exception.PaymentNotFoundException;
import payment.usecase.port.in.PaymentReleaser;
import payment.usecase.port.out.PaymentIEPublisher;
import payment.usecase.port.out.PaymentRepository;

import java.time.Instant;

public class ReleasePayment implements PaymentReleaser {

    private final PaymentRepository repository;
    private final PaymentIEPublisher publisher;

    public ReleasePayment(PaymentRepository repository, PaymentIEPublisher publisher) {
        this.repository = repository;
        this.publisher = publisher;
    }

    @Override
    public ReleasePaymentResponse release(ReleasePaymentRequest request) {
        PaymentId id = PaymentId.of(request.paymentId());
        Payment payment = repository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found: " + id.value()));
        payment.release(Instant.now());
        repository.save(payment);
        publisher.publishReleased(id);
        return new ReleasePaymentResponse(id.value(), payment.status().name());
    }
}
