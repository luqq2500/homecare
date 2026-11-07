package ie;

import java.time.Instant;

public record PaymentHeldIE(String paymentId, Instant occurredAt) implements IntegrationEvent {}
