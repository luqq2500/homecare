package ie;

import java.time.Instant;

public record ReservationExpiredIE(String reservationId, Instant occurredAt) implements IntegrationEvent {}
