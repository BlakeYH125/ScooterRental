package org.scooterrental.model.exception;

public class PasswordMismatchException extends RuntimeException {
    public PasswordMismatchException() {
        super("Неверно указан старый пароль");
    }
}
