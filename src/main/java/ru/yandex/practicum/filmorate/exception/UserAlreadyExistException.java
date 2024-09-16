package ru.yandex.practicum.filmorate.exception;

public class UserAlreadyExistException extends RuntimeException {
    public UserAlreadyExistException(String key, String value) {
        super("User with " + key + "=" + value + " already exist");
    }
}
