package booking.domain.exception;

public class InActiveProviderException extends RuntimeException {
    public InActiveProviderException(String s) {
        super(s);
    }
}
