package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

@Slf4j
@RestController
public class UserController {
    public Map<Integer, User> users = new HashMap<>();
    public static int count = 1;
    private final String PATH_USERS = "/users";

    @PostMapping(PATH_USERS)
    public User createUser(@RequestBody User user) {
        validate(user);
        user.setId(generateId());
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @PutMapping(PATH_USERS)
    public User updateUser(@RequestBody User user) {
        validate(user);
        User userToUpdate = users.get(user.getId());
        if (isNull(userToUpdate)) {
            throw new NotFoundException("Пользователь с id " + user.getId() + " не найден");
        }
        update(user, userToUpdate);
        return userToUpdate;
    }

    @GetMapping(PATH_USERS)
    public List<User> getAllUsers() {
        Collection<User> listOfUsers = users.values();
        return listOfUsers.stream().toList();
    }

    private void update(User user, User userToUpdate) {
        userToUpdate.setBirthday(user.getBirthday());
        userToUpdate.setName(user.getName());
        userToUpdate.setEmail(user.getEmail());
        userToUpdate.setLogin(user.getLogin());
    }

    private void validate(User user) {
        if (isNull(user.getEmail()) || user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
            log.error("Email пуст, либо не содержит знак @ {}", user.getEmail());
            throw new ValidationException("Поле email должно быть заполнено и содержать знак @");
        }
        if (isNull(user.getLogin()) || user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            log.error("Login пуст, либо содержит в себе пробелы {}", user.getLogin());
            throw new ValidationException("Поле login должен быть заполнен и не должен содержать пробелы");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Дата рождения позже сегодняшней даты {}", user.getBirthday());
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        if (isNull(user.getName()) || user.getName().isEmpty()) {
            log.warn("Вместо пустого имени присваивается логин");
            user.setName(user.getLogin());
        }
    }

    private int generateId() {
        return count++;
    }
}
