package org.scooterrental.model.exception;

public class ScooterInWarehouseException extends RuntimeException {
    public ScooterInWarehouseException() {
        super("Самокат находится на складе и недоступен для аренды");
    }
}
