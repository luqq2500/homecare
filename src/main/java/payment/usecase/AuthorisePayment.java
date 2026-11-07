package payment.usecase;

import payment.domain.Payment;
import payment.domain.PaymentId;
import payment.usecase.dto.AuthorisePaymentRequest;
import payment.usecase.dto.AuthorisePaymentResponse;
import payment.usecase.port.in.PaymentAuthoriser;
import payment.usecase.port.out.PaymentGateway;
import payment.usecase.port.out.PaymentIEPublisher;
import payment.usecase.port.out.PaymentRepository;

public class AuthorisePayment implements PaymentAuthoriser {

    private final PaymentRepository repository;
    private final PaymentIEPublisher publisher;
    private final PaymentGateway gateway;

    public AuthorisePayment(PaymentRepository repository, PaymentIEPublisher publisher, PaymentGateway gateway) {
        this.repository = repository;
        this.publisher = publisher;
        this.gateway = gateway;
    }

    @Override
    public AuthorisePaymentResponse authorise(AuthorisePaymentRequest request) {
        PaymentId id = PaymentId.generate();
        gateway.authorise(request.reservationId(), request.amount());
        Payment payment = Payment.authorise(id, request.reservationId(), request.amount());
        repository.save(payment);
        publisher.publishAuthorised(id);
        return new AuthorisePaymentResponse(id.value(), payment.status().name());
    }
}
