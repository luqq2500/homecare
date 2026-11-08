package payment.usecase;

import ie.ReservationRequestedIE;
import ie.PaymentAuthorisedIE;
import payment.domain.Payment;
import payment.domain.PaymentId;
import payment.usecase.port.in.ReservationRequestedHandler;
import payment.usecase.port.out.PaymentRepository;
import payment.usecase.port.out.PaymentGateway;
import payment.usecase.port.out.PaymentIEPublisher;

import java.time.Instant;

public class HandleReservationRequested implements ReservationRequestedHandler {

    private final PaymentRepository repository;
    private final PaymentGateway gateway;
    private final PaymentIEPublisher publisher;

    public HandleReservationRequested(PaymentRepository repository, PaymentGateway gateway, PaymentIEPublisher publisher) {
        this.repository = repository;
        this.gateway = gateway;
        this.publisher = publisher;
    }

    @Override
    public void handle(ReservationRequestedIE event) {
        if (repository.findByReservationId(event.reservationId()).isPresent()) {
            return;
        }
        PaymentId id = PaymentId.generate();
        Payment payment = Payment.authorise(id, event.reservationId(), event.amount());
        gateway.authorise(event.reservationId(), event.amount());
        repository.save(payment);
        publisher.publish(new PaymentAuthorisedIE(id.value(), Instant.now()));
    }
}
