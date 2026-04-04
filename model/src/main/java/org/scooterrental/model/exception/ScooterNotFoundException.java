package org.scooterrental.model.exception;

public class ScooterNotFoundException extends RuntimeException {
    public ScooterNotFoundException() {
        super("Самоката с таким id не существует");
    }
}
