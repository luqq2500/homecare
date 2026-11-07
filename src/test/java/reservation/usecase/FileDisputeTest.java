package reservation.usecase;

import org.junit.jupiter.api.Test;
import reservation.domain.*;
import reservation.usecase.dto.FileDisputeRequest;
import reservation.usecase.dto.FileDisputeResponse;
import reservation.usecase.exception.ReservationNotFoundException;
import reservation.usecase.port.out.ReservationRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FileDisputeTest {

    private final ReservationRepository repository = mock(ReservationRepository.class);
    private final FileDispute usecase = new FileDispute(repository);

    @Test
    void fileDisputeOnReservedReservationTransitionsToDisputed() {
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

        FileDisputeRequest request = new FileDisputeRequest(
                id.value(), "PROBLEM_DURING_JOB", "Missing helper");

        FileDisputeResponse response = usecase.file(request);

        assertEquals(id.value(), response.reservationId());
        assertEquals("DISPUTED", response.status());
        assertEquals("PROBLEM_DURING_JOB", response.disputeType());
        verify(repository).save(reservation);
    }

    @Test
    void fileDisputeOnNonExistentReservationThrowsNotFound() {
        ReservationId id = ReservationId.generate();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ReservationNotFoundException.class,
                () -> usecase.file(new FileDisputeRequest(id.value(), "PROBLEM_DURING_JOB", "reason")));
    }
}
