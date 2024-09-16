package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.friend.FriendStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;

import static java.util.Objects.isNull;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;
    private final FriendStorage friendStorage;


    @Autowired
    public UserServiceImpl(@Qualifier("H2UserDb") UserStorage userStorage, FriendStorage friendStorage) {
        this.userStorage = userStorage;
        this.friendStorage = friendStorage;
    }

    @Override
    public void addFriend(Integer userId, Integer userFriendId) {
        try {
            friendStorage.addFriend(userId, userFriendId);
        } catch (Exception ex) {
            throw new NotFoundException("Ошибка поиска пользователя");
        }
    }

    @Override
    public void deleteFriend(Integer userId, Integer userFriendId) {
        getUserById(userId);
        getUserById(userFriendId);

        friendStorage.deleteFriend(userId, userFriendId);
    }

    @Override
    public List<User> getMutualFriends(Integer userId, Integer userFriendId) {
        return friendStorage.getMutualFriends(userId, userFriendId);
    }

    @Override
    public User createUser(User user) {
        validate(user);
        return userStorage.createUser(user);
    }

    @Override
    public User updateUser(User user) {
        validate(user);
        User updateUser = userStorage.updateUser(user);
        if (isNull(updateUser)) {
            throw new NotFoundException("Пользователя с таким id не существует");
        }
        return updateUser;
    }

    @Override
    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    @Override
    public User getUserById(Integer id) {
        User user = userStorage.getUserById(id);
        if (isNull(user)) {
            throw new NotFoundException("Пользователя с таким id не существует");
        }
        return user;
    }

    @Override
    public void deleteUser(User user) {
        userStorage.deleteUser(user);
    }

    @Override
    public List<User> getAllUserFriends(Integer id) {
        getUserById(id);
        return friendStorage.getAllUserFriends(id);
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
