package reservation.usecase.port.in;

import reservation.usecase.dto.DisputeReservationRequest;
import reservation.usecase.dto.DisputeReservationResponse;

public interface ReservationDisputer {
    DisputeReservationResponse dispute(DisputeReservationRequest request);
}
