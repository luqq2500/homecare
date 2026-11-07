package reservation.usecase;

import org.junit.jupiter.api.Test;
import reservation.domain.*;
import reservation.usecase.dto.ExpireReservationRequest;
import reservation.usecase.dto.ExpireReservationResponse;
import reservation.usecase.exception.ReservationNotFoundException;
import reservation.usecase.port.out.ReservationRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExpireReservationTest {

    private final ReservationRepository repository = mock(ReservationRepository.class);
    private final ExpireReservation usecase = new ExpireReservation(repository);

    @Test
    void expireAfterDeadlineTransitionsToExpired() {
        ReservationId id = ReservationId.generate();
        Instant requestedAt = Instant.now().minusSeconds(25 * 3600);
        Reservation reservation = Reservation.request(
                id, requestedAt,
                BookedWindow.of(Instant.parse("2026-11-08T14:00:00Z"), 2),
                HoursFee.of(new BigDecimal("25.00"), 1, 2),
                TravelFee.ZERO,
                CancellationTier.STANDARD
        );
        when(repository.findById(id)).thenReturn(Optional.of(reservation));

        ExpireReservationResponse response = usecase.expire(new ExpireReservationRequest(id.value()));

        assertEquals("EXPIRED", response.status());
        verify(repository).save(reservation);
    }

    @Test
    void expireNonExistentReservationThrowsNotFound() {
        ReservationId id = ReservationId.generate();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ReservationNotFoundException.class,
                () -> usecase.expire(new ExpireReservationRequest(id.value())));
    }
}
