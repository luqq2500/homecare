package ie;

import java.time.Instant;

public record CheckInRecordedIE(String reservationId, Instant occurredAt) implements IntegrationEvent {}
