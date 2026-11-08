package customer.usecase;

import customer.domain.Customer;
import customer.usecase.port.out.CustomerRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RegisterCustomerTest {

    private final CustomerRepository repository = mock(CustomerRepository.class);
    private final RegisterCustomer usecase = new RegisterCustomer(repository);

    @Test
    void registerSavesCustomerAndReturnsId() {
        String customerId = usecase.register("Aina");
        assertNotNull(customerId);
        verify(repository).save(any(Customer.class));
    }
}
