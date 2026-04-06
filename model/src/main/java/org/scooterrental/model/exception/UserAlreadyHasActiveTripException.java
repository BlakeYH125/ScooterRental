package org.scooterrental.model.exception;

public class UserAlreadyHasActiveTripException extends RuntimeException {
    public UserAlreadyHasActiveTripException() {
        super("У пользователя уже есть активная поездка");
    }
}
