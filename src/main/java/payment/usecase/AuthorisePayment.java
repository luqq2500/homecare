package payment.usecase;

import payment.domain.Payment;
import payment.domain.PaymentId;
import payment.usecase.dto.AuthorisePaymentRequest;
import payment.usecase.dto.AuthorisePaymentResponse;
import payment.usecase.port.in.PaymentAuthoriser;
import payment.usecase.port.out.PaymentIEPublisher;
import payment.usecase.port.out.PaymentRepository;

public class AuthorisePayment implements PaymentAuthoriser {

    private final PaymentRepository repository;
    private final PaymentIEPublisher publisher;

    public AuthorisePayment(PaymentRepository repository, PaymentIEPublisher publisher) {
        this.repository = repository;
        this.publisher = publisher;
    }

    @Override
    public AuthorisePaymentResponse authorise(AuthorisePaymentRequest request) {
        PaymentId id = PaymentId.generate();
        Payment payment = Payment.authorise(id, request.reservationId(), request.amount());
        repository.save(payment);
        publisher.publishAuthorised(id);
        return new AuthorisePaymentResponse(id.value(), payment.status().name());
    }
}
