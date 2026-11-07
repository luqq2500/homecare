package ie;

import java.time.Instant;

public record DisputeFiledIE(String reservationId, Instant occurredAt) implements IntegrationEvent {}
