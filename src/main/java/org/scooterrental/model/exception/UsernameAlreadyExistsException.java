package org.scooterrental.model.exception;

public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException() {
        super("Аккаунт с таким именем пользователя уже существует");
    }
}
