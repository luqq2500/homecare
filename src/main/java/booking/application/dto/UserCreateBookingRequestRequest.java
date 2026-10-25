package booking.application.dto;

import java.time.LocalDateTime;
import java.util.List;

public record UserCreateBookingRequestRequest(
        String userId,
        String providerId,
        String categoryId,
        List<String> offeringIds,
        LocalDateTime start,
        LocalDateTime end
) {
}
