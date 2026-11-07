package reservation.usecase;

import reservation.domain.Reservation;
import reservation.domain.ReservationId;
import reservation.usecase.dto.AcceptReservationRequest;
import reservation.usecase.dto.AcceptReservationResponse;
import reservation.usecase.exception.ReservationNotFoundException;
import reservation.usecase.port.in.ReservationAcceptor;
import reservation.usecase.port.out.ReservationIEPublisher;
import reservation.usecase.port.out.ReservationRepository;

import java.time.Instant;

public class AcceptReservation implements ReservationAcceptor {

    private final ReservationRepository repository;
    private final ReservationIEPublisher publisher;

    public AcceptReservation(ReservationRepository repository, ReservationIEPublisher publisher) {
        this.repository = repository;
        this.publisher = publisher;
    }

    @Override
    public AcceptReservationResponse accept(AcceptReservationRequest request) {
        ReservationId id = ReservationId.of(request.reservationId());
        Reservation reservation = repository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found: " + id.value()));

        reservation.accept(Instant.now());
        repository.save(reservation);
        publisher.publishAccepted(id);

        return new AcceptReservationResponse(id.value(), reservation.status().name());
    }
}
