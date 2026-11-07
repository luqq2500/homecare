package reservation.usecase;

import reservation.domain.Reservation;
import reservation.domain.ReservationId;
import reservation.usecase.dto.DisputeReservationRequest;
import reservation.usecase.dto.DisputeReservationResponse;
import reservation.usecase.exception.ReservationNotFoundException;
import reservation.usecase.port.in.ReservationDisputer;
import reservation.usecase.port.out.ReservationRepository;

public class DisputeReservation implements ReservationDisputer {

    private final ReservationRepository repository;

    public DisputeReservation(ReservationRepository repository) {
        this.repository = repository;
    }

    @Override
    public DisputeReservationResponse dispute(DisputeReservationRequest request) {
        ReservationId id = ReservationId.of(request.reservationId());
        Reservation reservation = repository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found: " + id.value()));

        reservation.markDisputed();
        repository.save(reservation);

        return new DisputeReservationResponse(id.value(), reservation.status().name());
    }
}
