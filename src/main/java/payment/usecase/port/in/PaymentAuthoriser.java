package payment.usecase.port.in;

import payment.usecase.dto.AuthorisePaymentRequest;
import payment.usecase.dto.AuthorisePaymentResponse;

public interface PaymentAuthoriser {
    AuthorisePaymentResponse authorise(AuthorisePaymentRequest request);
}
