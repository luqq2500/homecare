package reservation.usecase;

import reservation.domain.*;
import reservation.usecase.dto.RequestReservationRequest;
import reservation.usecase.dto.RequestReservationResponse;
import reservation.usecase.port.in.ReservationRequester;
import reservation.usecase.port.out.ReservationRepository;
import reservation.usecase.port.out.ReservationIEPublisher;
import ie.ReservationRequestedIE;

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
        Reservation reservation = Reservation.request(
                id,
                Instant.now(),
                BookedWindow.of(request.start(), request.durationHours()),
                HoursFee.of(request.hourlyRate(), request.helpers(), request.durationHours()),
                TravelFee.of(request.travelFee()),
                CancellationTier.valueOf(request.cancellationTier())
        );
        repository.save(reservation);
        publisher.publish(new ReservationRequestedIE(id.value(), Instant.now()));
        return new RequestReservationResponse(id.value(), reservation.status().name());
    }
}
