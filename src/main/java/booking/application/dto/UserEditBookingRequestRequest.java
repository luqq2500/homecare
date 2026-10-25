package booking.application.dto;

import java.time.LocalDateTime;
import java.util.List;

public record UserEditBookingRequestRequest(
        String userId,
        String bookingId,
        List<String> newOfferingIds,
        LocalDateTime newStart,
        LocalDateTime newEnd
) {
}
