package payment.usecase;

import ie.ReservationAcceptedIE;
import ie.PaymentHeldIE;
import payment.domain.Payment;
import payment.usecase.exception.PaymentNotFoundException;
import payment.usecase.port.in.ReservationAcceptedHandler;
import payment.usecase.port.out.PaymentRepository;
import payment.usecase.port.out.PaymentGateway;
import payment.usecase.port.out.PaymentIEPublisher;

import java.time.Instant;

public class HandleReservationAccepted implements ReservationAcceptedHandler {

    private final PaymentRepository repository;
    private final PaymentGateway gateway;
    private final PaymentIEPublisher publisher;

    public HandleReservationAccepted(PaymentRepository repository, PaymentGateway gateway, PaymentIEPublisher publisher) {
        this.repository = repository;
        this.gateway = gateway;
        this.publisher = publisher;
    }

    @Override
    public void handle(ReservationAcceptedIE event) {
        Payment payment = repository.findByReservationId(event.reservationId())
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found for reservation: " + event.reservationId()));
        payment.hold(Instant.now());
        gateway.capture(event.reservationId());
        repository.save(payment);
        publisher.publish(new PaymentHeldIE(payment.id().value(), Instant.now()));
    }
}
