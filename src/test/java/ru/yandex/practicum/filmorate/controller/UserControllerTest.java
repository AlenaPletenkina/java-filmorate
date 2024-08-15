
package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@SpringBootTest
public class UserControllerTest {
    private User user;
    @Autowired
    private UserController userController;

    @Test
    void validateNotCreateUserWithEmptyEmail() {
        user = User.builder()
                .email("")
                .login("Alena")
                .name("Алена")
                .birthday(LocalDate.of(1993, 6, 4))
                .build();

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

        User userCreate = userController.createUser(user);
        Assertions.assertEquals(userCreate.getName(), userCreate.getLogin(), "Вместо имени присвоен login");
    }
}

