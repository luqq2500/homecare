package ie;

import java.time.Instant;

public record ReservationRequestedIE(String reservationId, Instant occurredAt) implements IntegrationEvent {}
