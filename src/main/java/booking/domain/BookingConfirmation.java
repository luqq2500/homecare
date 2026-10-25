package booking.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record BookingConfirmation(
        String id,
        String providerId,
        String bookingId,
        List<String> offeringIds,
        LocalDateTime start,
        LocalDateTime end,
        List<Map<String, Integer>> lineItemQuotation,
        int travelFee
){
    public int getPriceTotal(){
        int sumLineItem = lineItemQuotation().stream()
                .flatMap((lineItem)->lineItem.values().stream())
                .mapToInt(Integer::intValue)
                .sum();
        return sumLineItem + travelFee;
    }
}
