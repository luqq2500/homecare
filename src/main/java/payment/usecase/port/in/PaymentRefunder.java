package payment.usecase.port.in;

import payment.usecase.dto.RefundPaymentRequest;
import payment.usecase.dto.RefundPaymentResponse;

public interface PaymentRefunder {
    RefundPaymentResponse refund(RefundPaymentRequest request);
}
