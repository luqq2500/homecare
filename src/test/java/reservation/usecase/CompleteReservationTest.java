package reservation.usecase;

import org.junit.jupiter.api.Test;
import reservation.domain.*;
import reservation.usecase.dto.CompleteReservationRequest;
import reservation.usecase.dto.CompleteReservationResponse;
import reservation.usecase.exception.ReservationNotFoundException;
import reservation.usecase.port.out.ReservationRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CompleteReservationTest {

    private final ReservationRepository repository = mock(ReservationRepository.class);
    private final CompleteReservation usecase = new CompleteReservation(repository);

    @Test
    void completeFromReservedTransitionsToCompleted() {
        ReservationId id = ReservationId.generate();
        Reservation reservation = Reservation.request(
                id, Instant.now(),
                BookedWindow.of(Instant.parse("2026-11-08T14:00:00Z"), 2),
                HoursFee.of(new BigDecimal("25.00"), 1, 2),
                TravelFee.ZERO,
                CancellationTier.STANDARD
        );
        reservation.accept(Instant.now());
        when(repository.findById(id)).thenReturn(Optional.of(reservation));

        CompleteReservationResponse response = usecase.complete(new CompleteReservationRequest(id.value()));

        assertEquals("COMPLETED", response.status());
        verify(repository).save(reservation);
    }

    @Test
    void completeNonExistentReservationThrowsNotFound() {
        ReservationId id = ReservationId.generate();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ReservationNotFoundException.class,
                () -> usecase.complete(new CompleteReservationRequest(id.value())));
    }
}
