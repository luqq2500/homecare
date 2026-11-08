package reservation.usecase;

import org.junit.jupiter.api.Test;
import reservation.domain.*;
import reservation.domain.exception.InvalidStatusTransitionException;
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
    void completeFromReservedAfterDisputeWindowTransitionsToCompleted() {
        ReservationId id = ReservationId.generate();
        Instant pastStart = Instant.now().minusSeconds(4 * 3600);
        Reservation reservation = Reservation.request(
                id, Instant.now().minusSeconds(5 * 3600),
                BookedWindow.of(pastStart, 1),
                HoursFee.of(new BigDecimal("25.00"), 1, 1),
                TravelFee.ZERO,
                CancellationTier.STANDARD
        );
        reservation.accept(Instant.now().minusSeconds(4 * 3600));
        reservation.markJobCompletedWithEvidence("ev", Instant.now().minusSeconds(3 * 3600));
        when(repository.findById(id)).thenReturn(Optional.of(reservation));

        CompleteReservationResponse response = usecase.complete(new CompleteReservationRequest(id.value()));

        assertEquals(id.value(), response.reservationId());
        assertEquals("COMPLETED", response.status());
        verify(repository).save(reservation);
    }

    @Test
    void completeFromDisputedBeforeResolutionIsRejected() {
        ReservationId id = ReservationId.generate();
        Reservation reservation = Reservation.request(
                id, Instant.now().minusSeconds(3600),
                BookedWindow.of(Instant.now().minusSeconds(1800), 1),
                HoursFee.of(new BigDecimal("25.00"), 1, 1),
                TravelFee.ZERO,
                CancellationTier.STANDARD
        );
        reservation.accept(Instant.now().minusSeconds(1800));
        reservation.markDisputed();
        when(repository.findById(id)).thenReturn(Optional.of(reservation));

        assertThrows(InvalidStatusTransitionException.class,
                () -> usecase.complete(new CompleteReservationRequest(id.value())));
    }

    @Test
    void completeNonExistentReservationThrowsNotFound() {
        ReservationId id = ReservationId.generate();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ReservationNotFoundException.class,
                () -> usecase.complete(new CompleteReservationRequest(id.value())));
    }
}
