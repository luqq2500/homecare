package ie;

import java.time.Instant;

public record AvailabilityUpdatedIE(String providerId, Instant occurredAt) implements IntegrationEvent {}
