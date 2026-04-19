package org.scooterrental.model.exception;

public class ScooterNotAvailableException extends RuntimeException {
    public ScooterNotAvailableException() {
        super("Самокат с таким ID недоступен");
    }
}
