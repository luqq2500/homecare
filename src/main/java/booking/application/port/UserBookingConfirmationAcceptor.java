package booking.application.port;

import booking.application.dto.UserAcceptBookingConfirmationRequest;
import booking.application.dto.UserAcceptBookingConfirmationResponse;

public interface UserBookingConfirmationAcceptor {
    UserAcceptBookingConfirmationResponse accept(UserAcceptBookingConfirmationRequest request);
}
