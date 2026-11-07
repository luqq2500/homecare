package reservation.usecase.port.in;

import reservation.usecase.dto.CompleteReservationRequest;
import reservation.usecase.dto.CompleteReservationResponse;

public interface ReservationCompleter {
    CompleteReservationResponse complete(CompleteReservationRequest request);
}
