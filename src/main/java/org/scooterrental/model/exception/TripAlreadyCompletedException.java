package org.scooterrental.model.exception;

public class TripAlreadyCompletedException extends RuntimeException {
    public TripAlreadyCompletedException() {
        super("Поездка с таким ID уже завершена");
    }
}
