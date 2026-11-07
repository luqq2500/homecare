package availability.usecase.port.out;

import ie.IntegrationEvent;

public interface AvailabilityIEPublisher {
    void publish(IntegrationEvent event);
}
