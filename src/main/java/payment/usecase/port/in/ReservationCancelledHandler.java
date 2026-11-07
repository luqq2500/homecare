package payment.usecase.port.in;

import ie.ReservationCancelledIE;

public interface ReservationCancelledHandler {
    void handle(ReservationCancelledIE event);
}
