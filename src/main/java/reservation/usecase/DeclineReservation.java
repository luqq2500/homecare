package reservation.usecase;

import reservation.domain.Reservation;
import reservation.domain.ReservationId;
import reservation.usecase.dto.DeclineReservationRequest;
import reservation.usecase.dto.DeclineReservationResponse;
import reservation.usecase.exception.ReservationNotFoundException;
import reservation.usecase.port.in.ReservationDecliner;
import reservation.usecase.port.out.ReservationRepository;

public class DeclineReservation implements ReservationDecliner {

    private final ReservationRepository repository;

    public DeclineReservation(ReservationRepository repository) {
        this.repository = repository;
    }

    @Override
    public DeclineReservationResponse decline(DeclineReservationRequest request) {
        ReservationId id = ReservationId.of(request.reservationId());
        Reservation reservation = repository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found: " + id.value()));

        reservation.decline();
        repository.save(reservation);

        return new DeclineReservationResponse(id.value(), reservation.status().name());
    }
}
