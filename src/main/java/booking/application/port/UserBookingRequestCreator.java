package booking.application.port;

import booking.application.dto.UserCreateBookingRequestRequest;
import booking.application.dto.UserCreateBookingRequestResponse;

public interface UserBookingRequestCreator {
    public UserCreateBookingRequestResponse request(UserCreateBookingRequestRequest request);
}
