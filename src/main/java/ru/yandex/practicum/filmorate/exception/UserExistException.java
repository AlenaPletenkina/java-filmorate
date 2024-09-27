package ru.yandex.practicum.filmorate.exception;

public class UserExistException extends RuntimeException {
    public UserExistException(String key, String value) {
        super(String.format("User with %s=%s already exists", key, value));
    }
}