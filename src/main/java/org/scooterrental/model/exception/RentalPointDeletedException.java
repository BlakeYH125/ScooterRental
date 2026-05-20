package org.scooterrental.model.exception;

public class RentalPointDeletedException extends RuntimeException {
    public RentalPointDeletedException() {
        super("Точка аренды с таким id удалена");
    }
}
