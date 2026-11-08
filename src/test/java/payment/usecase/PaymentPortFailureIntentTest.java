package payment.usecase;

import ie.ReservationRequestedIE;
import ie.ReservationAcceptedIE;
import ie.ReservationCancelledIE;
import payment.usecase.exception.PaymentNotFoundException;
import payment.usecase.port.out.PaymentRepository;
import payment.usecase.port.out.PaymentGateway;
import payment.usecase.port.out.PaymentIEPublisher;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentPortFailureIntentTest {

    private final PaymentRepository repository = mock(PaymentRepository.class);
    private final PaymentGateway gateway = mock(PaymentGateway.class);
    private final PaymentIEPublisher publisher = mock(PaymentIEPublisher.class);

    @Test
    void handleAcceptedThrowsWhenPaymentMissing() {
        HandleReservationAccepted handler = new HandleReservationAccepted(repository, gateway, publisher);
        when(repository.findByReservationId("res-1")).thenReturn(Optional.empty());

        assertThrows(PaymentNotFoundException.class,
                () -> handler.handle(new ReservationAcceptedIE("res-1", "prov-1", new BigDecimal("100.00"), Instant.now())));
    }

    @Test
    void handleCancelledThrowsWhenPaymentMissing() {
        HandleReservationCancelled handler = new HandleReservationCancelled(repository, gateway, publisher);
        when(repository.findByReservationId("res-1")).thenReturn(Optional.empty());

        assertThrows(PaymentNotFoundException.class,
                () -> handler.handle(new ReservationCancelledIE(
                        "res-1",
                        new BigDecimal("20.00"),
                        new BigDecimal("80.00"),
                        BigDecimal.ZERO,
                        Instant.now()
                )));
    }

    @Test
    void handleRequestedDoesNotSaveWhenGatewayFails() {
        HandleReservationRequested handler = new HandleReservationRequested(repository, gateway, publisher);
        when(repository.findByReservationId("res-1")).thenReturn(Optional.empty());
        doThrow(new RuntimeException("gateway down")).when(gateway).authorise(any(), any());

        assertThrows(RuntimeException.class,
                () -> handler.handle(new ReservationRequestedIE("res-1", "cust-1", "prov-1", new BigDecimal("100.00"), Instant.now())));
        verify(repository, never()).save(any());
    }
}
