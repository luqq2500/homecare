package booking.domain;

public class InvalidPriceException extends RuntimeException {
    public InvalidPriceException(String s) {
        super(s);
    }
}
