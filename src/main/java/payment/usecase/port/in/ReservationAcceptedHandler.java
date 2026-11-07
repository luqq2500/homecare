package payment.usecase.port.in;

import ie.ReservationAcceptedIE;

public interface ReservationAcceptedHandler {
    void handle(ReservationAcceptedIE event);
}
