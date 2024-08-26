package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.Objects.isNull;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public void addFriend(Integer userId, Integer userFriendId) {
        User user = userStorage.getUserById(userId);
        User userFriend = userStorage.getUserById(userFriendId);
        user.getFriends().add(userFriendId);
        userFriend.getFriends().add(user.getId());
    }

    @Override
    public void deleteFriend(Integer userId, Integer userFriendId) {
        User user = userStorage.getUserById(userId);
        User userFriend = userStorage.getUserById(userFriendId);
        user.getFriends().remove(userFriend.getId());
        userFriend.getFriends().remove(user.getId());
    }

    @Override
    public List<User> getMutualFriends(Integer userId, Integer userFriendId) {
        User user = userStorage.getUserById(userId);
        User userFriend = userStorage.getUserById(userFriendId);
        Set<Integer> friends = user.getFriends();
        Set<Integer> secondUserFriends = userFriend.getFriends();
        List<User> mutualFriends = new ArrayList<>();
        for (Integer idFriends : friends) {
            if (secondUserFriends.contains(idFriends)) {
                mutualFriends.add(userStorage.getUserById(idFriends));
            }
        }
        return mutualFriends;
    }

    @Override
    public User createUser(User user) {
        validate(user);
        return userStorage.createUser(user);
    }

    @Override
    public User updateUser(User user) {
        validate(user);
        return userStorage.updateUser(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    @Override
    public User getUserById(Integer id) {
        return userStorage.getUserById(id);
    }

    @Override
    public void deleteUser(User user) {
        userStorage.deleteUser(user);
    }

    @Override
    public List<User> getAllUserFriends(Integer id) {
        Set<Integer> friends = userStorage.getUserById(id).getFriends();
        List<User> users = new ArrayList<>();
        for (Integer friend : friends) {
            User user = userStorage.getUserById(friend);
            users.add(user);
        }
        return users;
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
}
