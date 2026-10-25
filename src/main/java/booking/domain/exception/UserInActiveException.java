package booking.domain.exception;

public class UserInActiveException extends RuntimeException {
    public UserInActiveException(String s) {
        super(s);
    }
}
