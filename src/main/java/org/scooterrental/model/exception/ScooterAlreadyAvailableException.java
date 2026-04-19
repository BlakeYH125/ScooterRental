package org.scooterrental.model.exception;

public class ScooterAlreadyAvailableException extends RuntimeException {
    public ScooterAlreadyAvailableException() {
        super("Самокат с таким id уже доступен для использования");
    }
}
