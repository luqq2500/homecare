package reservation.usecase;

import reservation.domain.GeoLocation;
import reservation.domain.Reservation;
import reservation.domain.ReservationId;
import reservation.usecase.dto.RecordCheckInRequest;
import reservation.usecase.dto.RecordCheckInResponse;
import reservation.usecase.exception.ReservationNotFoundException;
import reservation.usecase.port.in.CheckInRecorder;
import reservation.usecase.port.out.ReservationRepository;

import java.time.Instant;

public class RecordCheckIn implements CheckInRecorder {

    private final ReservationRepository repository;

    public RecordCheckIn(ReservationRepository repository) {
        this.repository = repository;
    }

    @Override
    public RecordCheckInResponse record(RecordCheckInRequest request) {
        ReservationId id = ReservationId.of(request.reservationId());
        Reservation reservation = repository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found: " + id.value()));

        GeoLocation provider = GeoLocation.of(request.providerLatitude(), request.providerLongitude());
        GeoLocation customer = GeoLocation.of(request.customerLatitude(), request.customerLongitude());

        reservation.recordCheckIn(Instant.now(), provider, customer);
        repository.save(reservation);

        return new RecordCheckInResponse(id.value(), reservation.status().name(), reservation.hasCheckedIn());
    }
}
