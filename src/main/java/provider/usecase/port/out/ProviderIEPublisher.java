package provider.usecase.port.out;

import ie.IntegrationEvent;

public interface ProviderIEPublisher {
    void publish(IntegrationEvent event);
}
