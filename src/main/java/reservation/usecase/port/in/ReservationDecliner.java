package reservation.usecase.port.in;

import reservation.usecase.dto.DeclineReservationRequest;
import reservation.usecase.dto.DeclineReservationResponse;

public interface ReservationDecliner {
    DeclineReservationResponse decline(DeclineReservationRequest request);
}
