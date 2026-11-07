package payment.usecase.port.in;

import payment.usecase.dto.ReleasePaymentRequest;
import payment.usecase.dto.ReleasePaymentResponse;

public interface PaymentReleaser {
    ReleasePaymentResponse release(ReleasePaymentRequest request);
}
