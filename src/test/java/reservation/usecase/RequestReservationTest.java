package reservation.usecase;

import org.junit.jupiter.api.Test;
import reservation.domain.*;
import reservation.usecase.dto.RequestReservationRequest;
import reservation.usecase.dto.RequestReservationResponse;
import reservation.usecase.port.out.ReservationIEPublisher;
import reservation.usecase.port.out.ReservationRepository;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RequestReservationTest {

    private final ReservationRepository repository = mock(ReservationRepository.class);
    private final ReservationIEPublisher publisher = mock(ReservationIEPublisher.class);
    private final RequestReservation usecase = new RequestReservation(repository, publisher);

    @Test
    void requestCreatesReservationSavesItAndPublishesRequestedEvent() {
        RequestReservationRequest request = new RequestReservationRequest(
                Instant.parse("2026-11-08T14:00:00Z"),
                2,
                new BigDecimal("25.00"),
                1,
                null,
                "STANDARD"
        );

        RequestReservationResponse response = usecase.request(request);

        assertNotNull(response.reservationId());
        assertEquals("REQUESTED", response.status());
        verify(repository).save(any(Reservation.class));
        verify(publisher).publishRequested(any(ReservationId.class));
    }
}
