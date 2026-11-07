package payment.usecase;

import ie.ReservationDeclinedIE;
import payment.domain.Payment;
import payment.usecase.port.out.PaymentRepository;
import payment.usecase.port.out.PaymentGateway;

import java.time.Instant;

public class HandleReservationDeclined {

    private final PaymentRepository repository;
    private final PaymentGateway gateway;

    public HandleReservationDeclined(PaymentRepository repository, PaymentGateway gateway) {
        this.repository = repository;
        this.gateway = gateway;
    }

    public void handle(ReservationDeclinedIE event) {
        repository.findByReservationId(event.reservationId()).ifPresent(payment -> {
            payment.voidAuthorisation(Instant.now());
            gateway.voidAuth(event.reservationId());
            repository.save(payment);
        });
    }
}
