package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    public Map<Integer, User> users = new HashMap<>();
    public static int count = 1;

    @Override
    public User createUser(User user) {
        user.setId(generateId());
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public User updateUser(User user) {
        User userToUpdate = users.get(user.getId());
        if (isNull(userToUpdate)) {
            throw new NotFoundException("Пользователь с id " + user.getId() + " не найден");
        }
        update(user, userToUpdate);
        return userToUpdate;
    }

    public List<User> getAllUsers() {
        Collection<User> listOfUsers = users.values();
        return listOfUsers.stream().toList();
    }

    @Override
    public User getUserById(Integer id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException(String.format("Пользователь с id %d не существует", id));
        }
        return users.get(id);
    }

    @Override
    public void deleteUser(User user) {
        if (!users.containsKey(user.getId())) {
            throw new ValidationException("Пользователь " + user + " не существует");
        }
        users.remove(user.getId());
    }

    @Override
    public void deleteUserById(Integer id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException(String.format("Пользователь с id %d не существует", id));
        }
        users.remove(id);
    }

    private void update(User user, User userToUpdate) {
        userToUpdate.setBirthday(user.getBirthday());
        userToUpdate.setName(user.getName());
        userToUpdate.setEmail(user.getEmail());
        userToUpdate.setLogin(user.getLogin());
    }

    private int generateId() {
        return count++;
    }
}