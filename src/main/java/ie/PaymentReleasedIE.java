package ie;

import java.time.Instant;

public record PaymentReleasedIE(String paymentId, Instant occurredAt) implements IntegrationEvent {}
