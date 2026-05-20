package org.scooterrental.model.exception;

public class RentalPointHasChildrenException extends RuntimeException {
    public RentalPointHasChildrenException() {
        super("У точки с таким id есть действующие дочерние точки");
    }
}
