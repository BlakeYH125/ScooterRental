package org.scooterrental.model.exception;

public class UserHasNoActiveSeasonTicketException extends RuntimeException {
    public UserHasNoActiveSeasonTicketException() {
        super("У пользователя с таким ID нет активного абонемента");
    }
}
