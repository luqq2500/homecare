package provider.usecase.dto;

import java.math.BigDecimal;
import java.util.List;

public record SetOfferingRequest(
        String providerId,
        List<String> jobs,
        List<String> tools,
        BigDecimal hourlyRatePerHelper,
        int minHours,
        int maxHours,
        int helperCount,
        String cancellationTier,
        boolean travelFeeIncluded
) {}
