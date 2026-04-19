package org.scooterrental.model.exception;

public class RentalPointNotFoundException extends RuntimeException {
    public RentalPointNotFoundException() {
        super("Точки аренды с таким id не существует");
    }
}
