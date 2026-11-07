package payment.usecase.port.out;

import ie.IntegrationEvent;

public interface PaymentIEPublisher {
    void publish(IntegrationEvent event);
}
