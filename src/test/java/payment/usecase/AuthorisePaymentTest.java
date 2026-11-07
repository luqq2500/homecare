package payment.usecase;

import org.junit.jupiter.api.Test;
import payment.domain.Payment;
import payment.domain.PaymentId;
import payment.usecase.dto.AuthorisePaymentRequest;
import payment.usecase.dto.AuthorisePaymentResponse;
import payment.usecase.port.out.PaymentGateway;
import payment.usecase.port.out.PaymentIEPublisher;
import payment.usecase.port.out.PaymentRepository;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthorisePaymentTest {

    private final PaymentRepository repository = mock(PaymentRepository.class);
    private final PaymentIEPublisher publisher = mock(PaymentIEPublisher.class);
    private final PaymentGateway gateway = mock(PaymentGateway.class);
    private final AuthorisePayment usecase = new AuthorisePayment(repository, publisher, gateway);

    @Test
    void authoriseCreatesPaymentCallsGatewaySavesAndPublishes() {
        AuthorisePaymentRequest request = new AuthorisePaymentRequest("res-1", new BigDecimal("100.00"));

        AuthorisePaymentResponse response = usecase.authorise(request);

        assertNotNull(response.paymentId());
        assertEquals("AUTHORISED", response.status());
        verify(gateway).authorise("res-1", new BigDecimal("100.00"));
        verify(repository).save(any(Payment.class));
        verify(publisher).publishAuthorised(any(PaymentId.class));
    }
}
