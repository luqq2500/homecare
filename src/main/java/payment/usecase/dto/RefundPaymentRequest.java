package payment.usecase.dto;

import java.math.BigDecimal;

public record RefundPaymentRequest(String paymentId, BigDecimal amount) {}
