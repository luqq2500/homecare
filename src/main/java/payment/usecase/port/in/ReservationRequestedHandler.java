package payment.usecase.port.in;

import ie.ReservationRequestedIE;

public interface ReservationRequestedHandler {
    void handle(ReservationRequestedIE event);
}
