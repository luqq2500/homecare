package ie;

import java.time.Instant;

public record ProviderOfferingUpdatedIE(String providerId, Instant occurredAt) implements IntegrationEvent {}
