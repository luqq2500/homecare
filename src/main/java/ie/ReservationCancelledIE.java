package ie;

import java.math.BigDecimal;
import java.time.Instant;

public record ReservationCancelledIE(
        String reservationId,
        BigDecimal toProvider,
        BigDecimal toCustomer,
        BigDecimal toHoma,
        Instant occurredAt
) implements IntegrationEvent {}
