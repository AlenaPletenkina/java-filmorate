package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.event.EventStorage;
import ru.yandex.practicum.filmorate.dao.friend.FriendStorage;
import ru.yandex.practicum.filmorate.dao.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;
    private final FriendStorage friendStorage;
    private final FilmDbStorage filmStorage;
    private final FilmService filmService;
    private final EventStorage eventStorage;


    @Autowired
    public UserServiceImpl(@Qualifier("H2UserDb") UserStorage userStorage, FriendStorage friendStorage, EventStorage eventStorage,
                           @Qualifier("H2FilmDb") FilmDbStorage filmStorage, FilmService filmService) {
        this.userStorage = userStorage;
        this.friendStorage = friendStorage;
        this.filmStorage = filmStorage;
        this.eventStorage = eventStorage;
        this.filmService = filmService;
    }

    @Override
    public void addFriend(Integer userId, Integer userFriendId) {
        try {
            friendStorage.addFriend(userId, userFriendId);
            log.info("Добавляем пользователю {} событие {} - {} с другом {}", userId, EventType.FRIEND, Operation.ADD, userFriendId);
            eventStorage.add(new Event(userId, EventType.FRIEND, userFriendId, Operation.ADD));
        } catch (Exception ex) {
            throw new NotFoundException("Ошибка поиска пользователя");
        }
    }

    @Override
    public void deleteFriend(Integer userId, Integer userFriendId) {
        getUserById(userId);
        getUserById(userFriendId);
        log.info("Добавляем пользователю {} событие {} - {} с другом {}", userId, EventType.FRIEND, Operation.REMOVE, userFriendId);
        eventStorage.add(new Event(userId, EventType.FRIEND, userFriendId, Operation.REMOVE));
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

    @Override
    public void deleteUserById(Integer id) {
        List<User> friends = friendStorage.getAllUserFriends(id);

        for (User friend : friends) {
            friendStorage.deleteFriend(id, friend.getId());
            friendStorage.deleteFriend(friend.getId(), id);
        }

        userStorage.deleteUserById(id);
        log.info("Пользователь успешно удалён");
    }

    public List<Event> getUserFeed(Integer id) {
        log.info("Проверяем создан ли пользователь с id {}", id);
        User user = getUserById(id);
        log.info("Пользователь с id {} найден:{}", id, user);
        log.info("Получаем лист событий найден:{}", eventStorage.getEventsByUserId(id));
        return eventStorage.getEventsByUserId(id);
    }


    public List<Film> getUsersRecommendations(Integer id) {
        List<Integer> recommendUserFilms = filmStorage.getUsersRecommendations(id);
        log.info("Нашел список фильмов для рекомендации");
        List<Integer> userFilms = filmStorage.getFilmsUserById(id);
        log.info("Получил список фильмов пользователя для рекомендации {}", id);
        recommendUserFilms.removeAll(userFilms);
        List<Film> recommendFilms = new ArrayList<>();

        for (Integer indexFilm : recommendUserFilms) {
            recommendFilms.add(filmService.getFilm(indexFilm));
        }
        return recommendFilms;
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
        log.info("Валидация пользователя прошла успешно");
    }
}