package payment.usecase.port.in;

import payment.usecase.dto.HoldPaymentRequest;
import payment.usecase.dto.HoldPaymentResponse;

public interface PaymentHolder {
    HoldPaymentResponse hold(HoldPaymentRequest request);
}
