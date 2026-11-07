package payment.usecase.port.out;

import payment.domain.PaymentId;

public interface PaymentIEPublisher {
    void publishAuthorised(PaymentId paymentId);
    void publishHeld(PaymentId paymentId);
    void publishReleased(PaymentId paymentId);
    void publishRefunded(PaymentId paymentId);
}
