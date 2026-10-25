package booking.application.port;

import booking.domain.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(String id);
}
