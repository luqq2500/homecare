package reservation.usecase.port.in;

import reservation.usecase.dto.RequestReservationRequest;
import reservation.usecase.dto.RequestReservationResponse;

public interface ReservationRequester {
    RequestReservationResponse request(RequestReservationRequest request);
}
