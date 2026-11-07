package payment.usecase;

import payment.domain.Payment;
import payment.domain.PaymentId;
import payment.usecase.dto.AuthorisePaymentRequest;
import payment.usecase.dto.AuthorisePaymentResponse;
import payment.usecase.port.in.PaymentAuthoriser;
import payment.usecase.port.out.PaymentRepository;
import payment.usecase.port.out.PaymentGateway;
import payment.usecase.port.out.PaymentIEPublisher;
import ie.PaymentAuthorisedIE;

import java.time.Instant;

public class AuthorisePayment implements PaymentAuthoriser {

    private final PaymentRepository repository;
    private final PaymentGateway gateway;
    private final PaymentIEPublisher publisher;

    public AuthorisePayment(PaymentRepository repository, PaymentGateway gateway, PaymentIEPublisher publisher) {
        this.repository = repository;
        this.gateway = gateway;
        this.publisher = publisher;
    }

    @Override
    public AuthorisePaymentResponse authorise(AuthorisePaymentRequest request) {
        PaymentId id = PaymentId.generate();
        Payment payment = Payment.authorise(id, request.amount());
        gateway.authorise(id.value(), request.amount());
        repository.save(payment);
        publisher.publish(new PaymentAuthorisedIE(id.value(), Instant.now()));
        return new AuthorisePaymentResponse(id.value(), payment.status().name());
    }
}
