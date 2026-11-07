package payment.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentStatusTest {

    @Test
    void statusEnumValuesMatchSpecification() {
        assertEquals(PaymentStatus.AUTHORISED, PaymentStatus.valueOf("AUTHORISED"));
        assertEquals(PaymentStatus.HELD, PaymentStatus.valueOf("HELD"));
        assertEquals(PaymentStatus.RELEASED, PaymentStatus.valueOf("RELEASED"));
        assertEquals(PaymentStatus.REFUNDED, PaymentStatus.valueOf("REFUNDED"));
    }
}
