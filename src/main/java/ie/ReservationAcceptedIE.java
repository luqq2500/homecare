package ie;

import java.time.Instant;

public record ReservationAcceptedIE(String reservationId, Instant occurredAt) implements IntegrationEvent {}
