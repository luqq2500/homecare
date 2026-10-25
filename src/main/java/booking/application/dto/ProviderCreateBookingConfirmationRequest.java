package booking.application.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record ProviderCreateBookingConfirmationRequest(
        String providerId,
        String bookingId,
        LocalDateTime start,
        LocalDateTime end,
        List<Map<List<String>, Integer>> quotes

) {
}
