package booking.application.dto;

public record UserAcceptBookingConfirmationRequest(
        String userId,
        String bookingConfirmationId,
        String bookingId
) {
}
