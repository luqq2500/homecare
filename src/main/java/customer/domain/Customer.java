package customer.domain;

import java.util.Objects;

public class Customer {

    private final CustomerId id;
    private final String name;
    private String paymentMethodToken;
    private String serviceAddress;

    private Customer(CustomerId id, String name) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
    }

    public static Customer register(CustomerId id, String name) {
        return new Customer(id, name);
    }

    public void setPaymentMethod(String token) {
        this.paymentMethodToken = Objects.requireNonNull(token);
    }

    public void setServiceAddress(String address) {
        this.serviceAddress = Objects.requireNonNull(address);
    }

    public CustomerId id() { return id; }
    public String name() { return name; }
    public String paymentMethodToken() { return paymentMethodToken; }
    public String serviceAddress() { return serviceAddress; }
}
