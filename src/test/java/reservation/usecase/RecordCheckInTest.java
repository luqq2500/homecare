package reservation.usecase;

import org.junit.jupiter.api.Test;
import reservation.domain.*;
import reservation.usecase.dto.RecordCheckInRequest;
import reservation.usecase.dto.RecordCheckInResponse;
import reservation.usecase.exception.ReservationNotFoundException;
import reservation.usecase.port.out.ReservationRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RecordCheckInTest {

    private final ReservationRepository repository = mock(ReservationRepository.class);
    private final RecordCheckIn usecase = new RecordCheckIn(repository);

    @Test
    void recordCheckInOnReservedReservationSucceeds() {
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

        RecordCheckInRequest request = new RecordCheckInRequest(
                id.value(), 3.1580, 101.7124, 3.1579, 101.7123);

        RecordCheckInResponse response = usecase.record(request);

        assertEquals(id.value(), response.reservationId());
        assertEquals("RESERVED", response.status());
        assertTrue(response.checkedIn());
        verify(repository).save(reservation);
    }

    @Test
    void recordCheckInOnNonExistentReservationThrowsNotFound() {
        ReservationId id = ReservationId.generate();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ReservationNotFoundException.class,
                () -> usecase.record(new RecordCheckInRequest(id.value(), 0, 0, 0, 0)));
    }
}
