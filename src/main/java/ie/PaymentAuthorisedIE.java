package ie;

import java.time.Instant;

public record PaymentAuthorisedIE(String paymentId, Instant occurredAt) implements IntegrationEvent {}
