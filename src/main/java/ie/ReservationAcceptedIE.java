package ie;

import java.math.BigDecimal;
import java.time.Instant;

public record ReservationAcceptedIE(
        String reservationId,
        String providerId,
        BigDecimal amount,
        Instant occurredAt
) implements IntegrationEvent {}
