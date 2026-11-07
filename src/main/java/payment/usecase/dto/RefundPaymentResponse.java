package payment.usecase.dto;

public record RefundPaymentResponse(String paymentId, String status, String refundedAmount) {}
