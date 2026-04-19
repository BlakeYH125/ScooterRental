package org.scooterrental.model.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super("Пользователя с таким id не существует");
    }
}
