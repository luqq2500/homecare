package ie;

import java.time.Instant;

public record DisputeResolvedIE(String reservationId, Instant occurredAt) implements IntegrationEvent {}
