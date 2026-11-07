package payment.usecase.port.out;

import java.math.BigDecimal;

public interface PaymentGateway {
    void authorise(String reservationId, BigDecimal amount);
    void capture(String paymentId);
    void release(String paymentId);
    void refund(String paymentId, BigDecimal amount);
}
