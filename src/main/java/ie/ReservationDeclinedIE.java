package ie;

import java.time.Instant;

public record ReservationDeclinedIE(String reservationId, Instant occurredAt) implements IntegrationEvent {}
