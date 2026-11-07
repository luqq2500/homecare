package reservation.usecase.port.in;

import reservation.usecase.dto.ExpireReservationRequest;
import reservation.usecase.dto.ExpireReservationResponse;

public interface ReservationExpirer {
    ExpireReservationResponse expire(ExpireReservationRequest request);
}
