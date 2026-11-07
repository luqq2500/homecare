package reservation.usecase;

import org.junit.jupiter.api.Test;
import reservation.domain.*;
import reservation.usecase.dto.DisputeReservationRequest;
import reservation.usecase.dto.DisputeReservationResponse;
import reservation.usecase.exception.ReservationNotFoundException;
import reservation.usecase.port.out.ReservationRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DisputeReservationTest {

    private final ReservationRepository repository = mock(ReservationRepository.class);
    private final DisputeReservation usecase = new DisputeReservation(repository);

    @Test
    void disputeFromReservedTransitionsToDisputed() {
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

        DisputeReservationResponse response = usecase.dispute(new DisputeReservationRequest(id.value()));

        assertEquals("DISPUTED", response.status());
        verify(repository).save(reservation);
    }

    @Test
    void disputeNonExistentReservationThrowsNotFound() {
        ReservationId id = ReservationId.generate();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ReservationNotFoundException.class,
                () -> usecase.dispute(new DisputeReservationRequest(id.value())));
    }
}
