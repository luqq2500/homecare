package customer.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    @Test
    void registerCreatesCustomerWithIdAndName() {
        CustomerId id = CustomerId.generate();
        Customer customer = Customer.register(id, "Aina");
        assertEquals(id, customer.id());
        assertEquals("Aina", customer.name());
        assertNull(customer.paymentMethodToken());
    }

    @Test
    void setPaymentMethodAndAddress() {
        Customer customer = Customer.register(CustomerId.generate(), "Aina");
        customer.setPaymentMethod("pm_123");
        customer.setServiceAddress("123 Main St");
        assertEquals("pm_123", customer.paymentMethodToken());
        assertEquals("123 Main St", customer.serviceAddress());
    }
}
