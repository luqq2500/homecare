package reservation.usecase.dto;

public record RecordCheckInRequest(String reservationId, double providerLatitude, double providerLongitude, double customerLatitude, double customerLongitude) {}
