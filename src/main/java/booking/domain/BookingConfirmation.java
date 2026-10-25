package booking.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record BookingConfirmation(
        String id,
        String providerId,
        String bookingId,
        LocalDateTime start,
        LocalDateTime end,
        List<Map<List<String>, Integer>> quotes
){
    public int getTotalPrice(){
        return this.quotes().stream()
                .flatMap((lineItem)->lineItem.values().stream())
                .mapToInt(Integer::intValue)
                .sum();
    }
}
