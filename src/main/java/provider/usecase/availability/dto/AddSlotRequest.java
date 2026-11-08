package provider.usecase.availability.dto;

public record AddSlotRequest(String providerId, String start, int durationHours) {}
