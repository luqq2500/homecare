package reservation.usecase.port.out;

import reservation.domain.ReservationId;

public interface ReservationIEPublisher {
    void publishRequested(ReservationId reservationId);
    void publishAccepted(ReservationId reservationId);
}
