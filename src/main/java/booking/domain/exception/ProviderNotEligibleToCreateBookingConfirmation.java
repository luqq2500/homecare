package booking.domain.exception;

public class ProviderNotEligibleToCreateBookingConfirmation extends RuntimeException {
    public ProviderNotEligibleToCreateBookingConfirmation(String s) {
        super(s);
    }
}
