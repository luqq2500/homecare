package reservation.usecase;

import org.junit.jupiter.api.Test;
import reservation.domain.*;
import reservation.usecase.dto.ExpireReservationRequest;
import reservation.usecase.dto.ExpireReservationResponse;
import reservation.usecase.exception.ReservationNotFoundException;
import reservation.usecase.port.out.ReservationIEPublisher;
import reservation.usecase.port.out.ReservationRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExpireReservationTest {

    private final ReservationRepository repository = mock(ReservationRepository.class);
    private final ReservationIEPublisher publisher = mock(ReservationIEPublisher.class);
    private final ExpireReservation usecase = new ExpireReservation(repository, publisher);

    @Test
    void expireAfterDeadlineTransitionsToExpired() {
        ReservationId id = ReservationId.generate();
        Instant requestedAt = Instant.now().minusSeconds(25 * 3600);
        Instant start = Instant.now().plusSeconds(4 * 3600);
        start = start.minusSeconds(start.getEpochSecond() % (15 * 60));
        Reservation reservation = Reservation.request(
                id, requestedAt,
                BookedWindow.of(start, 2),
                HoursFee.of(new BigDecimal("25.00"), 1, 2),
                TravelFee.ZERO,
                CancellationTier.STANDARD
        );
        when(repository.findById(id)).thenReturn(Optional.of(reservation));

        ExpireReservationResponse response = usecase.expire(new ExpireReservationRequest(id.value()));

        assertEquals("EXPIRED", response.status());
        verify(repository).save(reservation);
        verify(publisher).publish(any());
    }

    @Test
    void expireNonExistentReservationThrowsNotFound() {
        ReservationId id = ReservationId.generate();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ReservationNotFoundException.class,
                () -> usecase.expire(new ExpireReservationRequest(id.value())));
    }
}
