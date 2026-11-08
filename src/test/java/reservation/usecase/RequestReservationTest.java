package reservation.usecase;

import ie.ReservationRequestedIE;
import org.junit.jupiter.api.Test;
import reservation.domain.*;
import reservation.usecase.dto.RequestReservationRequest;
import reservation.usecase.dto.RequestReservationResponse;
import reservation.usecase.port.out.ReservationIEPublisher;
import reservation.usecase.port.out.ReservationRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RequestReservationTest {

    private final ReservationRepository repository = mock(ReservationRepository.class);
    private final ReservationIEPublisher publisher = mock(ReservationIEPublisher.class);
    private final RequestReservation usecase = new RequestReservation(repository, publisher);

    @Test
    void requestCreatesReservationSavesItAndPublishesRequestedEvent() {
        Instant start = Instant.now().plusSeconds(4 * 3600);
        start = start.minusSeconds(start.getEpochSecond() % (15 * 60));
        RequestReservationRequest request = new RequestReservationRequest(
                start,
                2,
                new BigDecimal("25.00"),
                1,
                BigDecimal.ZERO,
                "STANDARD",
                "cust-1",
                "prov-1",
                List.of("Kitchen")
        );

        RequestReservationResponse response = usecase.request(request);

        assertNotNull(response.reservationId());
        assertEquals("REQUESTED", response.status());
        verify(repository).save(any(Reservation.class));
        verify(publisher).publish(argThat(event -> event instanceof ReservationRequestedIE));
    }
}
