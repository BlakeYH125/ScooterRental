package org.scooterrental.model.exception;

public class RentalPointNotEmptyException extends RuntimeException {
    public RentalPointNotEmptyException() {
        super("На точке аренды с этим ID находятся самокаты");
    }
}
