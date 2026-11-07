package ie;

import java.time.Instant;

public record ProviderFineAppliedIE(String reservationId, String providerId, Instant occurredAt) implements IntegrationEvent {}
