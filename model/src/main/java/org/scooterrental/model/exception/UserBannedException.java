package org.scooterrental.model.exception;

public class UserBannedException extends RuntimeException {
    public UserBannedException() {
        super("Пользователь с таким id заблокирован");
    }
}
