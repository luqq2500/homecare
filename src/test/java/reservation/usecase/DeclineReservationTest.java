package reservation.usecase;

import org.junit.jupiter.api.Test;
import reservation.domain.*;
import reservation.usecase.dto.DeclineReservationRequest;
import reservation.usecase.dto.DeclineReservationResponse;
import reservation.usecase.exception.ReservationNotFoundException;
import reservation.usecase.port.out.ReservationRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeclineReservationTest {

    private final ReservationRepository repository = mock(ReservationRepository.class);
    private final DeclineReservation usecase = new DeclineReservation(repository);

    @Test
    void declineFromRequestedTransitionsToDeclined() {
        ReservationId id = ReservationId.generate();
        Reservation reservation = Reservation.request(
                id, Instant.now(),
                BookedWindow.of(Instant.parse("2026-11-08T14:00:00Z"), 2),
                HoursFee.of(new BigDecimal("25.00"), 1, 2),
                TravelFee.ZERO,
                CancellationTier.STANDARD
        );
        when(repository.findById(id)).thenReturn(Optional.of(reservation));

        DeclineReservationResponse response = usecase.decline(new DeclineReservationRequest(id.value()));

        assertEquals("DECLINED", response.status());
        verify(repository).save(reservation);
    }

    @Test
    void declineNonExistentReservationThrowsNotFound() {
        ReservationId id = ReservationId.generate();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ReservationNotFoundException.class,
                () -> usecase.decline(new DeclineReservationRequest(id.value())));
    }
}
