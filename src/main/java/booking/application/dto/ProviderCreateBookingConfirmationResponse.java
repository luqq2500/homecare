package booking.application.dto;

import booking.domain.BookingConfirmation;

public record ProviderCreateBookingConfirmationResponse(
        BookingConfirmation bookingConfirmation
) {
}
