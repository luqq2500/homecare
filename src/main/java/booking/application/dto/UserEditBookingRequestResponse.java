package booking.application.dto;

import booking.domain.Booking;


public record UserEditBookingRequestResponse(
        Booking booking
){}
