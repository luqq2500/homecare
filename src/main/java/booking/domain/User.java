package booking.domain;

import booking.domain.exception.UserNotEligibleToAcceptBookingConfirmationException;
import booking.domain.exception.UserNotEligibleToCancelBookingException;
import booking.domain.exception.UserNotEligibleToRequestBookingException;

public class User {
    private final String id;
    private UserStatus status;

    public User(String id, UserStatus status){
        this.id=id;
        this.status=status;
    }

    public void validateRequestBookingEligibility() throws RuntimeException {
        if (this.status.equals(UserStatus.INACTIVE)){
            throw new UserNotEligibleToRequestBookingException("User is not eligible to request a booking.");
        }
    }

    public void validateAcceptBookingConfirmationEligibility() throws RuntimeException {
        if (this.status.equals(UserStatus.INACTIVE)){
            throw new UserNotEligibleToAcceptBookingConfirmationException("User is not eligible to accept a booking confirmation.");
        }
    }

    public void validateCancelBookingEligibility() throws RuntimeException {
        if (this.status.equals(UserStatus.INACTIVE)){
            throw new UserNotEligibleToCancelBookingException("User is not eligible to cancel a booking.");
        }
    }

    public String getId() {
        return id;
    }
}
