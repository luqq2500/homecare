package booking.application.port;

import booking.application.dto.UserEditBookingRequestRequest;
import booking.application.dto.UserEditBookingRequestResponse;

public interface UserBookingRequestEditor {
    public UserEditBookingRequestResponse edit(UserEditBookingRequestRequest request);
}
