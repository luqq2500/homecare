package reservation.usecase.port.in;

import reservation.usecase.dto.CancelReservationRequest;
import reservation.usecase.dto.CancelReservationResponse;

public interface ReservationCanceller {
    CancelReservationResponse cancel(CancelReservationRequest request);
}
