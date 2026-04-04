package org.scooterrental.model.exception;

public class TariffNotFoundException extends RuntimeException {
    public TariffNotFoundException() {
        super("Тарифа с таким id не существует");
    }
}
