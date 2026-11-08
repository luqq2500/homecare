package reservation.usecase;

import org.junit.jupiter.api.Test;
import reservation.domain.*;
import reservation.usecase.dto.CancelReservationRequest;
import reservation.usecase.dto.CancelReservationResponse;
import reservation.usecase.exception.ReservationNotFoundException;
import reservation.usecase.port.out.ReservationIEPublisher;
import reservation.usecase.port.out.ReservationRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CancelReservationTest {

    private final ReservationRepository repository = mock(ReservationRepository.class);
    private final ReservationIEPublisher publisher = mock(ReservationIEPublisher.class);
    private final CancelReservation usecase = new CancelReservation(repository, publisher);

    @Test
    void customerCancelFromRequestedTransitionsToCancelled() {
        ReservationId id = ReservationId.generate();
        Instant now = Instant.now();
        Instant start = now.plusSeconds(4 * 3600);
        start = start.minusSeconds(start.getEpochSecond() % (15 * 60));
        Reservation reservation = Reservation.request(
                id, now.minusSeconds(3600),
                BookedWindow.of(start, 2),
                HoursFee.of(new BigDecimal("25.00"), 1, 2),
                TravelFee.ZERO,
                CancellationTier.STANDARD
        );
        when(repository.findById(id)).thenReturn(Optional.of(reservation));

        CancelReservationResponse response = usecase.cancel(new CancelReservationRequest(id.value(), "CUSTOMER"));

        assertEquals("CANCELLED", response.status());
        verify(repository).save(reservation);
        verify(publisher).publish(any());
    }

    @Test
    void cancelNonExistentReservationThrowsNotFound() {
        ReservationId id = ReservationId.generate();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ReservationNotFoundException.class,
                () -> usecase.cancel(new CancelReservationRequest(id.value(), "CUSTOMER")));
    }
}
