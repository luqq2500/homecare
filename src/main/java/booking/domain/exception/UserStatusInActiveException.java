package booking.domain.exception;

public class UserStatusInActiveException extends RuntimeException {
    public UserStatusInActiveException(String s) {
        super(s);
    }
}
