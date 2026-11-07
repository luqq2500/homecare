package ie;

import java.time.Instant;

public record JobCompletionSubmittedIE(String reservationId, Instant occurredAt) implements IntegrationEvent {}
