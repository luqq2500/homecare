package booking.application.exception;

public class ProviderNotFoundException extends RuntimeException {
    public ProviderNotFoundException(String s) {
        super(s);
    }
}
