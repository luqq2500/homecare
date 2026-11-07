package reservation.usecase.dto;

public record RecordCheckInResponse(String reservationId, String status, boolean checkedIn) {}
