package payment.usecase.port.out;

import payment.domain.Payment;
import payment.domain.PaymentId;

import java.util.Optional;

public interface PaymentRepository {
    void save(Payment payment);
    Optional<Payment> findById(PaymentId id);
    Optional<Payment> findByReservationId(String reservationId);
}
