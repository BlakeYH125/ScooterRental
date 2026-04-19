package org.scooterrental.model.exception;

public class ValueLessZeroException extends RuntimeException {
    public ValueLessZeroException() {
        super("Введено значение меньше 0");
    }
    public ValueLessZeroException(String message) {
        super(message);
    }

}
