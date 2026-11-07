package reservation.usecase.port.out;

import ie.IntegrationEvent;

public interface ReservationIEPublisher {
    void publish(IntegrationEvent event);
}
