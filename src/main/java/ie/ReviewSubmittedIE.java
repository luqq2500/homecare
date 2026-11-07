package ie;

import java.time.Instant;

public record ReviewSubmittedIE(String reservationId, String providerId, Instant occurredAt) implements IntegrationEvent {}
