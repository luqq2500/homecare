package ie;

import java.time.Instant;

public record PaymentRefundedIE(String paymentId, Instant occurredAt) implements IntegrationEvent {}
