package org.scooterrental.model.exception;

public class WrongPasswordException extends RuntimeException {
    public WrongPasswordException() {
        super("Неверный пароль");
    }
}
