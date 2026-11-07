package reservation.usecase;

import reservation.domain.*;
import reservation.usecase.dto.RequestReservationRequest;
import reservation.usecase.dto.RequestReservationResponse;
import reservation.usecase.port.in.ReservationRequester;
import reservation.usecase.port.out.ReservationIEPublisher;
import reservation.usecase.port.out.ReservationRepository;

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
        BookedWindow window = BookedWindow.of(request.bookedStart(), request.bookedHours());
        HoursFee hoursFee = HoursFee.of(request.hourlyRate(), request.numberOfHelpers(), request.bookedHours());
        TravelFee travelFee = request.travelFeeAmount() != null
                ? TravelFee.of(request.travelFeeAmount())
                : TravelFee.ZERO;
        CancellationTier tier = CancellationTier.valueOf(request.cancellationTier());

        Reservation reservation = Reservation.request(
                id,
                Instant.now(),
                window,
                hoursFee,
                travelFee,
                tier
        );

        repository.save(reservation);
        publisher.publishRequested(id);

        return new RequestReservationResponse(id.value(), reservation.status().name());
    }
}
