package booking.application.dto;

import booking.domain.Booking;

public record UserCreateBookingRequestResponse(
        Booking booking
) {
}
