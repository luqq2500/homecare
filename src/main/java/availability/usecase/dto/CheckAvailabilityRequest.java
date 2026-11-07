package availability.usecase.dto;

import java.time.Instant;

public record CheckAvailabilityRequest(String providerId, Instant start, int durationHours) {}
