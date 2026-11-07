package reservation.usecase;

import reservation.domain.Reservation;
import reservation.domain.ReservationId;
import reservation.usecase.dto.MarkJobCompletedRequest;
import reservation.usecase.dto.MarkJobCompletedResponse;
import reservation.usecase.exception.ReservationNotFoundException;
import reservation.usecase.port.in.JobCompletionMarker;
import reservation.usecase.port.out.ReservationRepository;

import java.time.Instant;

public class MarkJobCompleted implements JobCompletionMarker {

    private final ReservationRepository repository;

    public MarkJobCompleted(ReservationRepository repository) {
        this.repository = repository;
    }

    @Override
    public MarkJobCompletedResponse markCompleted(MarkJobCompletedRequest request) {
        ReservationId id = ReservationId.of(request.reservationId());
        Reservation reservation = repository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found: " + id.value()));

        // Action only — status stays RESERVED
        reservation.markJobCompletedWithEvidence(Instant.now());
        repository.save(reservation);

        return new MarkJobCompletedResponse(id.value(), reservation.status().name());
    }
}
