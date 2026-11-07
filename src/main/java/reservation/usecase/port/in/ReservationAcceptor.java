package reservation.usecase.port.in;

import reservation.usecase.dto.AcceptReservationRequest;
import reservation.usecase.dto.AcceptReservationResponse;

public interface ReservationAcceptor {
    AcceptReservationResponse accept(AcceptReservationRequest request);
}
