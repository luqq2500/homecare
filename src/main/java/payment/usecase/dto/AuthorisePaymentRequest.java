package payment.usecase.dto;

import java.math.BigDecimal;

public record AuthorisePaymentRequest(String reservationId, BigDecimal amount) {}
