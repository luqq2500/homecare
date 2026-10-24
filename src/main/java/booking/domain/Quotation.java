package booking.domain;

import java.util.List;

public record Quotation(
        String id,
        String providerId,
        String categoryId,
        List<String> offeringIds,
        Schedule schedule,
        BookingPrice price
)
{
    public void validate() throws RuntimeException{
        schedule.validate();
    }
}
