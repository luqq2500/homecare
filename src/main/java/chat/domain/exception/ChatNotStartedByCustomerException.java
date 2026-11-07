package chat.domain.exception;

public class ChatNotStartedByCustomerException extends RuntimeException {
    public ChatNotStartedByCustomerException(String message) {
        super(message);
    }
}
