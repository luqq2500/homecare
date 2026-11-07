package reservation.usecase;

import reservation.domain.DisputeType;
import reservation.domain.Reservation;
import reservation.domain.ReservationId;
import reservation.usecase.dto.FileDisputeRequest;
import reservation.usecase.dto.FileDisputeResponse;
import reservation.usecase.exception.ReservationNotFoundException;
import reservation.usecase.port.in.DisputeFiler;
import reservation.usecase.port.out.ReservationRepository;

import java.time.Instant;

public class FileDispute implements DisputeFiler {

    private final ReservationRepository repository;

    public FileDispute(ReservationRepository repository) {
        this.repository = repository;
    }

    @Override
    public FileDisputeResponse file(FileDisputeRequest request) {
        ReservationId id = ReservationId.of(request.reservationId());
        Reservation reservation = repository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found: " + id.value()));

        DisputeType type = DisputeType.valueOf(request.type());
        reservation.fileDispute(type, request.reason(), Instant.now());
        repository.save(reservation);

        return new FileDisputeResponse(id.value(), reservation.status().name(), type.name());
    }
}
