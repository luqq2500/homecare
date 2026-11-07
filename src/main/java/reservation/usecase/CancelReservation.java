package reservation.usecase;

import reservation.domain.Reservation;
import reservation.domain.ReservationId;
import reservation.usecase.dto.CancelReservationRequest;
import reservation.usecase.dto.CancelReservationResponse;
import reservation.usecase.exception.ReservationNotFoundException;
import reservation.usecase.port.in.ReservationCanceller;
import reservation.usecase.port.out.ReservationRepository;

import java.time.Instant;

public class CancelReservation implements ReservationCanceller {

    private final ReservationRepository repository;

    public CancelReservation(ReservationRepository repository) {
        this.repository = repository;
    }

    @Override
    public CancelReservationResponse cancel(CancelReservationRequest request) {
        ReservationId id = ReservationId.of(request.reservationId());
        Reservation reservation = repository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found: " + id.value()));

        Instant now = Instant.now();
        if ("CUSTOMER".equalsIgnoreCase(request.cancelledBy())) {
            reservation.cancelByCustomer(now);
        } else {
            reservation.cancelByProvider(now);
        }
        repository.save(reservation);

        return new CancelReservationResponse(id.value(), reservation.status().name());
    }
}
