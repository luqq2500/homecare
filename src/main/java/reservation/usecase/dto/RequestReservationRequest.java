package reservation.usecase.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record RequestReservationRequest(
        Instant bookedStart,
        int bookedHours,
        BigDecimal hourlyRate,
        int numberOfHelpers,
        BigDecimal travelFeeAmount,
        String cancellationTier
) {}
