package payment.usecase;

import ie.ReservationRequestedIE;
import ie.ReservationAcceptedIE;
import ie.ReservationDeclinedIE;
import ie.ReservationCancelledIE;
import payment.domain.Payment;
import payment.domain.PaymentId;
import payment.domain.PaymentStatus;
import payment.usecase.port.out.PaymentRepository;
import payment.usecase.port.out.PaymentGateway;
import payment.usecase.port.out.PaymentIEPublisher;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentHandlerIntentTest {

    private final PaymentRepository repository = mock(PaymentRepository.class);
    private final PaymentGateway gateway = mock(PaymentGateway.class);
    private final PaymentIEPublisher publisher = mock(PaymentIEPublisher.class);

    @Test
    void handleRequestedAuthorisesPayment() {
        HandleReservationRequested handler = new HandleReservationRequested(repository, gateway, publisher);
        when(repository.findByReservationId("res-1")).thenReturn(Optional.empty());

        handler.handle(new ReservationRequestedIE("res-1", "cust-1", "prov-1", new BigDecimal("100.00"), Instant.now()));

        verify(gateway).authorise("res-1", new BigDecimal("100.00"));
        verify(repository).save(any(Payment.class));
        verify(publisher).publish(any());
    }

    @Test
    void handleRequestedIsIdempotent() {
        HandleReservationRequested handler = new HandleReservationRequested(repository, gateway, publisher);
        Payment existing = Payment.authorise(PaymentId.generate(), "res-1", new BigDecimal("100.00"));
        when(repository.findByReservationId("res-1")).thenReturn(Optional.of(existing));

        handler.handle(new ReservationRequestedIE("res-1", "cust-1", "prov-1", new BigDecimal("100.00"), Instant.now()));

        verify(gateway, never()).authorise(any(), any());
        verify(repository, never()).save(any());
    }

    @Test
    void handleAcceptedHoldsPayment() {
        HandleReservationAccepted handler = new HandleReservationAccepted(repository, gateway, publisher);
        Payment payment = Payment.authorise(PaymentId.generate(), "res-1", new BigDecimal("100.00"));
        when(repository.findByReservationId("res-1")).thenReturn(Optional.of(payment));

        handler.handle(new ReservationAcceptedIE("res-1", "prov-1", new BigDecimal("100.00"), Instant.now()));

        assertEquals(PaymentStatus.HELD, payment.status());
        verify(gateway).capture("res-1");
        verify(repository).save(payment);
    }

    @Test
    void handleDeclinedVoidsAuthorisation() {
        HandleReservationDeclined handler = new HandleReservationDeclined(repository, gateway);
        Payment payment = Payment.authorise(PaymentId.generate(), "res-1", new BigDecimal("100.00"));
        when(repository.findByReservationId("res-1")).thenReturn(Optional.of(payment));

        handler.handle(new ReservationDeclinedIE("res-1", Instant.now()));

        assertEquals(PaymentStatus.VOIDED, payment.status());
        verify(gateway).voidAuth("res-1");
        verify(repository).save(payment);
    }

    @Test
    void handleCancelledSettlesAndRefunds() {
        HandleReservationCancelled handler = new HandleReservationCancelled(repository, gateway, publisher);
        Payment payment = Payment.authorise(PaymentId.generate(), "res-1", new BigDecimal("100.00"));
        payment.hold(Instant.now());
        when(repository.findByReservationId("res-1")).thenReturn(Optional.of(payment));

        handler.handle(new ReservationCancelledIE(
                "res-1",
                new BigDecimal("20.00"),
                new BigDecimal("80.00"),
                BigDecimal.ZERO,
                new BigDecimal("100.00"),
                Instant.now()
        ));

        verify(gateway).refund("res-1", new BigDecimal("80.00"));
        verify(repository).save(payment);
        verify(publisher).publish(any());
    }
}
