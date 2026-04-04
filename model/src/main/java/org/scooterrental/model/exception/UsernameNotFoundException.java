package org.scooterrental.model.exception;

public class UsernameNotFoundException extends RuntimeException {
    public UsernameNotFoundException() {
        super("Аккаунта с таким именем пользователя не существует");
    }
}
