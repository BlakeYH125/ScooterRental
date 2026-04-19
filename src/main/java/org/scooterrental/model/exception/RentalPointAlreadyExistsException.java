package org.scooterrental.model.exception;

public class RentalPointAlreadyExistsException extends RuntimeException {
    public RentalPointAlreadyExistsException() {
        super("Точка аренды с такой локацией уже есть");
    }
}
