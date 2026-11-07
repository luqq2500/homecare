package reservation.usecase.port.out;

import reservation.domain.Reservation;
import reservation.domain.ReservationId;

import java.util.Optional;

public interface ReservationRepository {
    void save(Reservation reservation);
    Optional<Reservation> findById(ReservationId id);
}
