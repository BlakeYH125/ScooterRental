package org.scooterrental.model.exception;

public class ScooterInServiceException extends RuntimeException {
    public ScooterInServiceException() {
        super("Самокат с таким id находится на обслуживании");
    }
}
