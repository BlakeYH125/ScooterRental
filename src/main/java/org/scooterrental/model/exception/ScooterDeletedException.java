package org.scooterrental.model.exception;

public class ScooterDeletedException extends RuntimeException {
    public ScooterDeletedException() {
        super("Самокат с таким id удален");
    }
}
