package org.scooterrental.model.exception;

public class TripNotFoundException extends RuntimeException {
    public TripNotFoundException() {
        super("Поездки с таким id не существует");
    }
}
