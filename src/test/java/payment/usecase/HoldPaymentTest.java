package payment.usecase;

import ie.PaymentHeldIE;
import org.junit.jupiter.api.Test;
import payment.domain.Payment;
import payment.domain.PaymentId;
import payment.usecase.dto.HoldPaymentRequest;
import payment.usecase.dto.HoldPaymentResponse;
import payment.usecase.exception.PaymentNotFoundException;
import payment.usecase.port.out.PaymentGateway;
import payment.usecase.port.out.PaymentIEPublisher;
import payment.usecase.port.out.PaymentRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HoldPaymentTest {

    private final PaymentRepository repository = mock(PaymentRepository.class);
    private final PaymentGateway gateway = mock(PaymentGateway.class);
    private final PaymentIEPublisher publisher = mock(PaymentIEPublisher.class);
    private final HoldPayment usecase = new HoldPayment(repository, gateway, publisher);

    @Test
    void holdExistingAuthorisedPaymentCapturesTransitionsToHeldAndPublishes() {
        PaymentId id = PaymentId.generate();
        Payment payment = Payment.authorise(id, "res-1", new BigDecimal("100.00"));
        when(repository.findById(id)).thenReturn(Optional.of(payment));

        HoldPaymentResponse response = usecase.hold(new HoldPaymentRequest(id.value()));

        assertEquals(id.value(), response.paymentId());
        assertEquals("HELD", response.status());
        verify(gateway).capture(id.value());
        verify(repository).save(payment);
        verify(publisher).publish(argThat(event -> event instanceof PaymentHeldIE));
    }

    @Test
    void holdNonExistentPaymentThrowsNotFound() {
        PaymentId id = PaymentId.generate();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(PaymentNotFoundException.class,
                () -> usecase.hold(new HoldPaymentRequest(id.value())));
        verify(gateway, never()).capture(any());
        verify(publisher, never()).publish(any());
    }
}
