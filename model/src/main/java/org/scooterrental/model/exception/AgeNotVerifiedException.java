package org.scooterrental.model.exception;

public class AgeNotVerifiedException extends RuntimeException {
    public AgeNotVerifiedException() {
        super("Возраст пользователя с таким id не подтвержден");
    }
}
