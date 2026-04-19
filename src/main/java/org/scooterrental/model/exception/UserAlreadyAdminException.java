package org.scooterrental.model.exception;

public class UserAlreadyAdminException extends RuntimeException {
    public UserAlreadyAdminException() {
        super("Пользователь с таким id уже является администратором");
    }
}
