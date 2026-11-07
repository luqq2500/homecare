package reservation.usecase;

import reservation.domain.Reservation;
import reservation.domain.ReservationId;
import reservation.usecase.dto.ExpireReservationRequest;
import reservation.usecase.dto.ExpireReservationResponse;
import reservation.usecase.exception.ReservationNotFoundException;
import reservation.usecase.port.in.ReservationExpirer;
import reservation.usecase.port.out.ReservationRepository;

import java.time.Instant;

public class ExpireReservation implements ReservationExpirer {

    private final ReservationRepository repository;

    public ExpireReservation(ReservationRepository repository) {
        this.repository = repository;
    }

    @Override
    public ExpireReservationResponse expire(ExpireReservationRequest request) {
        ReservationId id = ReservationId.of(request.reservationId());
        Reservation reservation = repository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found: " + id.value()));

        reservation.expire(Instant.now());
        repository.save(reservation);

        return new ExpireReservationResponse(id.value(), reservation.status().name());
    }
}
