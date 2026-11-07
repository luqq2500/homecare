package reservation.usecase;

import reservation.domain.*;
import reservation.usecase.dto.RequestReservationRequest;
import reservation.usecase.dto.RequestReservationResponse;
import reservation.usecase.port.in.ReservationRequester;
import reservation.usecase.port.out.ReservationRepository;
import reservation.usecase.port.out.ReservationIEPublisher;
import ie.ReservationRequestedIE;

import java.math.BigDecimal;
import java.time.Instant;

public class RequestReservation implements ReservationRequester {

    private final ReservationRepository repository;
    private final ReservationIEPublisher publisher;

    public RequestReservation(ReservationRepository repository, ReservationIEPublisher publisher) {
        this.repository = repository;
        this.publisher = publisher;
    }

    @Override
    public RequestReservationResponse request(RequestReservationRequest request) {
        ReservationId id = ReservationId.generate();
        PricingSnapshot snapshot = PricingSnapshot.of(
                request.hourlyRate(), request.numberOfHelpers(), request.bookedHours(),
                false, request.travelFeeAmount(), CancellationTier.valueOf(request.cancellationTier())
        );
        Reservation reservation = Reservation.requestWithIdentity(
                id, Instant.now(),
                BookedWindow.of(request.bookedStart(), request.bookedHours()),
                HoursFee.of(request.hourlyRate(), request.numberOfHelpers(), request.bookedHours()),
                TravelFee.of(request.travelFeeAmount()),
                CancellationTier.valueOf(request.cancellationTier()),
                request.customerId(), request.providerId(),
                request.jobs(), snapshot
        );
        repository.save(reservation);
        BigDecimal total = reservation.hoursFee().amount().add(reservation.travelFee().amount());
        publisher.publish(new ReservationRequestedIE(id.value(), request.customerId(), request.providerId(), total, Instant.now()));
        return new RequestReservationResponse(id.value(), reservation.status().name());
    }
}
