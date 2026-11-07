package payment.usecase;

import ie.ReservationCancelledIE;
import ie.PaymentRefundedIE;
import payment.domain.Payment;
import payment.usecase.exception.PaymentNotFoundException;
import payment.usecase.port.in.ReservationCancelledHandler;
import payment.usecase.port.out.PaymentRepository;
import payment.usecase.port.out.PaymentGateway;
import payment.usecase.port.out.PaymentIEPublisher;

import java.time.Instant;

public class HandleReservationCancelled implements ReservationCancelledHandler {

    private final PaymentRepository repository;
    private final PaymentGateway gateway;
    private final PaymentIEPublisher publisher;

    public HandleReservationCancelled(PaymentRepository repository, PaymentGateway gateway, PaymentIEPublisher publisher) {
        this.repository = repository;
        this.gateway = gateway;
        this.publisher = publisher;
    }

    @Override
    public void handle(ReservationCancelledIE event) {
        Payment payment = repository.findByReservationId(event.reservationId())
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found for reservation: " + event.reservationId()));
        payment.settle(event.toProvider(), event.toCustomer(), Instant.now());
        if (event.toCustomer().signum() > 0) {
            gateway.refund(event.reservationId(), event.toCustomer());
        }
        repository.save(payment);
        publisher.publish(new PaymentRefundedIE(payment.id().value(), Instant.now()));
    }
}
