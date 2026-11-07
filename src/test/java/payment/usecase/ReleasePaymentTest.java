package payment.usecase;

import org.junit.jupiter.api.Test;
import payment.domain.Payment;
import payment.domain.PaymentId;
import payment.usecase.dto.ReleasePaymentRequest;
import payment.usecase.dto.ReleasePaymentResponse;
import payment.usecase.exception.PaymentNotFoundException;
import payment.usecase.port.out.PaymentGateway;
import payment.usecase.port.out.PaymentIEPublisher;
import payment.usecase.port.out.PaymentRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReleasePaymentTest {

    private final PaymentRepository repository = mock(PaymentRepository.class);
    private final PaymentIEPublisher publisher = mock(PaymentIEPublisher.class);
    private final PaymentGateway gateway = mock(PaymentGateway.class);
    private final ReleasePayment usecase = new ReleasePayment(repository, publisher, gateway);

    @Test
    void releaseExistingHeldPaymentTransitionsToReleasedAndPublishes() {
        PaymentId id = PaymentId.generate();
        Payment payment = Payment.authorise(id, "res-1", new BigDecimal("100.00"));
        payment.hold(Instant.now());
        when(repository.findById(id)).thenReturn(Optional.of(payment));

        ReleasePaymentResponse response = usecase.release(new ReleasePaymentRequest(id.value()));

        assertEquals(id.value(), response.paymentId());
        assertEquals("RELEASED", response.status());
        verify(gateway).release(id.value());
        verify(repository).save(payment);
        verify(publisher).publishReleased(id);
    }

    @Test
    void releaseNonExistentPaymentThrowsNotFound() {
        PaymentId id = PaymentId.generate();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(PaymentNotFoundException.class,
                () -> usecase.release(new ReleasePaymentRequest(id.value())));
        verify(gateway, never()).release(any());
    }
}
