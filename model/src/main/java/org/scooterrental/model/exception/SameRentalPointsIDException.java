package org.scooterrental.model.exception;

public class SameRentalPointsIDException extends RuntimeException {
    public SameRentalPointsIDException() {
        super("Точка не может быть родительской для самой себя");
    }
}
