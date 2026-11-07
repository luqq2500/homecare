package availability.usecase.dto;

import java.time.Instant;

public record AddSlotRequest(String providerId, Instant start, int durationHours) {}
