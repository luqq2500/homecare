package reservation.usecase;

import reservation.domain.Reservation;
import reservation.domain.ReservationId;
import reservation.usecase.dto.CompleteReservationRequest;
import reservation.usecase.dto.CompleteReservationResponse;
import reservation.usecase.exception.ReservationNotFoundException;
import reservation.usecase.port.in.ReservationCompleter;
import reservation.usecase.port.out.ReservationRepository;

import java.time.Instant;

public class CompleteReservation implements ReservationCompleter {

    private final ReservationRepository repository;

    public CompleteReservation(ReservationRepository repository) {
        this.repository = repository;
    }

    @Override
    public CompleteReservationResponse complete(CompleteReservationRequest request) {
        ReservationId id = ReservationId.of(request.reservationId());
        Reservation reservation = repository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found: " + id.value()));

        // Only path to COMPLETED: dispute window closed cleanly or Homa resolved the report
        reservation.moveToCompletedAfterDisputeWindowOrResolution(Instant.now());
        repository.save(reservation);

        return new CompleteReservationResponse(id.value(), reservation.status().name());
    }
}
