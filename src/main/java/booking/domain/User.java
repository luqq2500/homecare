package booking.domain;

import booking.domain.exception.UserInActiveException;

public class User {
    private final String id;
    private UserStatus status;

    public User(String id, UserStatus status){
        this.id=id;
        this.status=status;
    }

    public void validateIsActive() throws RuntimeException {
        if (this.status.equals(UserStatus.INACTIVE)){
            throw new UserInActiveException("User is no longer active.");
        }
    }

    public String getId() {
        return id;
    }

    public void inactiveUser(){
        this.status = UserStatus.INACTIVE;
    }
}
