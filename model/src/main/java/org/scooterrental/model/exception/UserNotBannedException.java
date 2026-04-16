package org.scooterrental.model.exception;

public class UserNotBannedException extends RuntimeException {
    public UserNotBannedException() {
        super("Пользователь с таким ID не имеет активных блокировок");
    }
}
