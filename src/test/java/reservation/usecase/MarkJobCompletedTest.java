package reservation.usecase;

import org.junit.jupiter.api.Test;
import reservation.domain.*;
import reservation.usecase.dto.MarkJobCompletedRequest;
import reservation.usecase.dto.MarkJobCompletedResponse;
import reservation.usecase.exception.ReservationNotFoundException;
import reservation.usecase.port.out.ReservationRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MarkJobCompletedTest {

    private final ReservationRepository repository = mock(ReservationRepository.class);
    private final MarkJobCompleted usecase = new MarkJobCompleted(repository);

    @Test
    void markJobCompletedKeepsStatusReserved() {
        ReservationId id = ReservationId.generate();
        Instant now = Instant.now();
        Instant pastStart = now.minusSeconds(2 * 3600);
        Reservation reservation = Reservation.request(
                id, now.minusSeconds(4 * 3600),
                BookedWindow.of(pastStart, 1),
                HoursFee.of(new BigDecimal("25.00"), 1, 1),
                TravelFee.ZERO,
                CancellationTier.STANDARD
        );
        reservation.accept(now.minusSeconds(3 * 3600));
        when(repository.findById(id)).thenReturn(Optional.of(reservation));

        MarkJobCompletedResponse response = usecase.markCompleted(new MarkJobCompletedRequest(id.value()));

        assertEquals(id.value(), response.reservationId());
        assertEquals("RESERVED", response.status());
        verify(repository).save(reservation);
    }

    @Test
    void markJobCompletedOnNonExistentReservationThrowsNotFound() {
        ReservationId id = ReservationId.generate();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ReservationNotFoundException.class,
                () -> usecase.markCompleted(new MarkJobCompletedRequest(id.value())));
    }
}
