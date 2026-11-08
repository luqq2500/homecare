package payment.usecase;

import ie.PaymentRefundedIE;
import org.junit.jupiter.api.Test;
import payment.domain.Payment;
import payment.domain.PaymentId;
import payment.usecase.dto.RefundPaymentRequest;
import payment.usecase.dto.RefundPaymentResponse;
import payment.usecase.exception.PaymentNotFoundException;
import payment.usecase.port.out.PaymentGateway;
import payment.usecase.port.out.PaymentIEPublisher;
import payment.usecase.port.out.PaymentRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RefundPaymentTest {

    private final PaymentRepository repository = mock(PaymentRepository.class);
    private final PaymentGateway gateway = mock(PaymentGateway.class);
    private final PaymentIEPublisher publisher = mock(PaymentIEPublisher.class);
    private final RefundPayment usecase = new RefundPayment(repository, gateway, publisher);

    @Test
    void refundExistingHeldPaymentRefundsAmountAndPublishes() {
        PaymentId id = PaymentId.generate();
        Payment payment = Payment.authorise(id, "res-1", new BigDecimal("100.00"));
        payment.hold(Instant.now());
        when(repository.findById(id)).thenReturn(Optional.of(payment));

        RefundPaymentResponse response = usecase.refund(
                new RefundPaymentRequest(id.value(), new BigDecimal("40.00")));

        assertEquals(id.value(), response.paymentId());
        assertEquals("HELD", response.status());
        assertEquals("40.00", response.refundedAmount());
        verify(gateway).refund(id.value(), new BigDecimal("40.00"));
        verify(repository).save(payment);
        verify(publisher).publish(argThat(event ->
                event instanceof PaymentRefundedIE &&
                ((PaymentRefundedIE) event).paymentId().equals(id.value())));
    }

    @Test
    void refundNonExistentPaymentThrowsNotFound() {
        PaymentId id = PaymentId.generate();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(PaymentNotFoundException.class,
                () -> usecase.refund(new RefundPaymentRequest(id.value(), new BigDecimal("10.00"))));
        verify(gateway, never()).refund(any(), any());
    }
}
