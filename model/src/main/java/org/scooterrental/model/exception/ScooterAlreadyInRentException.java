package org.scooterrental.model.exception;

public class ScooterAlreadyInRentException extends RuntimeException {
    public ScooterAlreadyInRentException() {
        super("Самокат с таким id уже находится в аренде");
    }
}
