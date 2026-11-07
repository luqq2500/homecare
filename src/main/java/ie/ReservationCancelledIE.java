package ie;

import java.time.Instant;

public record ReservationCancelledIE(String reservationId, Instant occurredAt) implements IntegrationEvent {}
