package reservation.usecase;

import ie.ReservationAcceptedIE;
import org.junit.jupiter.api.Test;
import reservation.domain.*;
import reservation.usecase.dto.AcceptReservationRequest;
import reservation.usecase.dto.AcceptReservationResponse;
import reservation.usecase.exception.ReservationNotFoundException;
import reservation.usecase.port.out.ReservationIEPublisher;
import reservation.usecase.port.out.ReservationRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AcceptReservationTest {

    private final ReservationRepository repository = mock(ReservationRepository.class);
    private final ReservationIEPublisher publisher = mock(ReservationIEPublisher.class);
    private final AcceptReservation usecase = new AcceptReservation(repository, publisher);

    @Test
    void acceptExistingRequestedReservationTransitionsToReservedAndPublishesEvent() {
        ReservationId id = ReservationId.generate();
        Instant now = Instant.now();
        Instant start = now.plusSeconds(4 * 3600);
        start = start.minusSeconds(start.getEpochSecond() % (15 * 60));
        Reservation reservation = Reservation.request(
                id,
                now.minusSeconds(3600),
                BookedWindow.of(start, 2),
                HoursFee.of(new BigDecimal("25.00"), 1, 2),
                TravelFee.ZERO,
                CancellationTier.STANDARD
        );
        when(repository.findById(id)).thenReturn(Optional.of(reservation));

        AcceptReservationResponse response = usecase.accept(new AcceptReservationRequest(id.value()));

        assertEquals(id.value(), response.reservationId());
        assertEquals("RESERVED", response.status());
        verify(repository).save(reservation);
        verify(publisher).publish(argThat(event -> event instanceof ReservationAcceptedIE));
    }

    @Test
    void acceptNonExistentReservationThrowsNotFound() {
        ReservationId id = ReservationId.generate();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ReservationNotFoundException.class,
                () -> usecase.accept(new AcceptReservationRequest(id.value())));
        verify(publisher, never()).publish(any());
    }
}
