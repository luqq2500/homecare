package customer.usecase.exception;

public class CustomerRegistrationException extends RuntimeException {
    public CustomerRegistrationException(String message) { super(message); }
}
