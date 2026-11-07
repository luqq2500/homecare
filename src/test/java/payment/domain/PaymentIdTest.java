package payment.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentIdTest {

    @Test
    void generateProducesUniqueIds() {
        PaymentId a = PaymentId.generate();
        PaymentId b = PaymentId.generate();
        assertNotEquals(a, b);
        assertNotNull(a.value());
    }

    @Test
    void ofAndEqualsHashCodeToString() {
        PaymentId a = PaymentId.of("pay-1");
        PaymentId b = PaymentId.of("pay-1");
        PaymentId c = PaymentId.of("pay-2");
        assertEquals(a, b);
        assertNotEquals(a, c);
        assertNotEquals(a, null);
        assertNotEquals(a, "pay-1");
        assertEquals(a.hashCode(), b.hashCode());
        assertEquals("pay-1", a.toString());
        assertEquals("pay-1", a.value());
    }
}
