package booking.domain;

public record BookingPrice(int amount){
    public void validate() throws RuntimeException {
        if (amount==0 || amount<0){
            throw new InvalidPriceException("Amount cannot be zero or less.");
        }
    }
}