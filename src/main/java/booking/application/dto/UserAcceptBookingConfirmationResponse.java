package booking.application.dto;

import booking.domain.Booking;

public record UserAcceptBookingConfirmationResponse(
        Booking booking
) {
}
