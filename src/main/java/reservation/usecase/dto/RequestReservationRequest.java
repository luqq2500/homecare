package reservation.usecase.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record RequestReservationRequest(
        Instant bookedStart,
        int bookedHours,
        BigDecimal hourlyRate,
        int numberOfHelpers,
        BigDecimal travelFeeAmount,
        String cancellationTier,
        String customerId,
        String providerId,
        List<String> jobs
) {}
