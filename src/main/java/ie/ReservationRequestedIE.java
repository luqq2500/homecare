package ie;

import java.math.BigDecimal;
import java.time.Instant;

public record ReservationRequestedIE(
        String reservationId,
        String customerId,
        String providerId,
        BigDecimal amount,
        Instant occurredAt
) implements IntegrationEvent {}
