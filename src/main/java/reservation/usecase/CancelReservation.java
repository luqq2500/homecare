package reservation.usecase;

import reservation.domain.Reservation;
import reservation.domain.ReservationId;
import reservation.usecase.dto.CancelReservationRequest;
import reservation.usecase.dto.CancelReservationResponse;
import reservation.usecase.exception.ReservationNotFoundException;
import reservation.usecase.port.in.ReservationCanceller;
import reservation.usecase.port.out.ReservationRepository;
import reservation.usecase.port.out.ReservationIEPublisher;
import ie.ReservationCancelledIE;

import java.math.BigDecimal;
import java.time.Instant;

public class CancelReservation implements ReservationCanceller {

    private final ReservationRepository repository;
    private final ReservationIEPublisher publisher;

    public CancelReservation(ReservationRepository repository, ReservationIEPublisher publisher) {
        this.repository = repository;
        this.publisher = publisher;
    }

    @Override
    public CancelReservationResponse cancel(CancelReservationRequest request) {
        ReservationId id = ReservationId.of(request.reservationId());
        Reservation reservation = repository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found: " + id.value()));
        Instant now = Instant.now();
        if ("CUSTOMER".equals(request.cancelledBy())) {
            reservation.cancelByCustomer(now);
        } else {
            reservation.cancelByProvider(now);
        }
        repository.save(reservation);
        BigDecimal total = reservation.hoursFee().amount().add(reservation.travelFee().amount());
        BigDecimal fee = reservation.customerCancellationFee(now);
        BigDecimal toProvider = fee;
        BigDecimal toCustomer = total.subtract(fee);
        BigDecimal toHoma = BigDecimal.ZERO;
        publisher.publish(new ReservationCancelledIE(id.value(), toProvider, toCustomer, toHoma, now));
        return new CancelReservationResponse(id.value(), reservation.status().name());
    }
}
