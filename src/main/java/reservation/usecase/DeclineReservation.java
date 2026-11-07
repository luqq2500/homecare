package reservation.usecase;

import reservation.domain.Reservation;
import reservation.domain.ReservationId;
import reservation.usecase.dto.DeclineReservationRequest;
import reservation.usecase.dto.DeclineReservationResponse;
import reservation.usecase.exception.ReservationNotFoundException;
import reservation.usecase.port.in.ReservationDecliner;
import reservation.usecase.port.out.ReservationRepository;
import reservation.usecase.port.out.ReservationIEPublisher;
import ie.ReservationDeclinedIE;

import java.time.Instant;

public class DeclineReservation implements ReservationDecliner {

    private final ReservationRepository repository;
    private final ReservationIEPublisher publisher;

    public DeclineReservation(ReservationRepository repository, ReservationIEPublisher publisher) {
        this.repository = repository;
        this.publisher = publisher;
    }

    @Override
    public DeclineReservationResponse decline(DeclineReservationRequest request) {
        ReservationId id = ReservationId.of(request.reservationId());
        Reservation reservation = repository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found: " + id.value()));
        reservation.decline();
        repository.save(reservation);
        publisher.publish(new ReservationDeclinedIE(id.value(), Instant.now()));
        return new DeclineReservationResponse(id.value(), reservation.status().name());
    }
}
