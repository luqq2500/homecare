package customer.usecase;

import customer.domain.Customer;
import customer.domain.CustomerId;
import customer.usecase.port.in.CustomerRegistrar;
import customer.usecase.port.out.CustomerRepository;

public class RegisterCustomer implements CustomerRegistrar {

    private final CustomerRepository repository;

    public RegisterCustomer(CustomerRepository repository) {
        this.repository = repository;
    }

    @Override
    public String register(String name) {
        CustomerId id = CustomerId.generate();
        Customer customer = Customer.register(id, name);
        repository.save(customer);
        return id.value();
    }
}
