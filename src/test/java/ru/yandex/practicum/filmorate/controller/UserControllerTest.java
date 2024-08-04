package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

public class UserControllerTest {
    User user;

    @Test
    void validateNotCreateUserWithEmptyEmail() {
        user = User.builder()
                .email("")
                .login("Alena")
                .name("Алена")
                .birthday(LocalDate.of(1993, 6, 4))
                .build();
        UserController userController = new UserController();
        Assertions.assertThrows(ValidationException.class, () -> {
            userController.createUser(user);
        }, "Пустой email");
    }

    @Test
    void validateNotCreateUserWithEmptyLogin() {
        user = User.builder()
                .email("Alenapletenkina@yandex.ru")
                .login("")
                .name("Алена")
                .birthday(LocalDate.of(1993, 6, 4))
                .build();
        UserController userController = new UserController();
        Assertions.assertThrows(ValidationException.class, () -> {
            userController.createUser(user);
        }, "Пустой login");
    }

    @Test
    void validateNotCreateUserWithWrongBirthday() {
        user = User.builder()
                .email("Alenapletenkina@yandex.ru")
                .login("Alena")
                .name("Алена")
                .birthday(LocalDate.of(2025, 6, 4))
                .build();
        UserController userController = new UserController();
        Assertions.assertThrows(ValidationException.class, () -> {
            userController.createUser(user);
        }, "Дата рождения раньше сегодняшней даты");
    }

    @Test
    void validateCreateUserWithLoginInsteadOfName() {
        user = User.builder()
                .email("Alenapletenkina@yandex.ru")
                .login("Alena")
                .name("")
                .birthday(LocalDate.of(1993, 6, 4))
                .build();

        UserController userController = new UserController();
        User userCreate = userController.createUser(user);
        Assertions.assertEquals(userCreate.getName(), userCreate.getLogin(), "Вместо имени присвоен login");
    }
}
