package reservation.usecase.dto;

public record CancelReservationRequest(String reservationId, String cancelledBy) {}
