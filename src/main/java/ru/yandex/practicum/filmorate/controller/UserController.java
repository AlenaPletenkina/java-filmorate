package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@Slf4j
@RestController
public class UserController {
    private final String path = "/users";
    private final UserService userService;
    private final ObjectMapper objectMapper;

    @Autowired
    public UserController(UserService userService, ObjectMapper objectMapper) {
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    @PostMapping(path)
    public User createUser(@RequestBody User user) {
        log.info("Получил запрос на создание пользователя {}", user);
        User createdUser = userService.createUser(user);
        try {
            log.info("Пользователь успешно создан, response:{}", objectMapper.writeValueAsString(createdUser));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return createdUser;
    }

    @PutMapping(path)
    public User updateUser(@RequestBody User user) {
        log.info("Получил запрос на обновление пользователя {}", user);
        return userService.updateUser(user);
    }

    @GetMapping(path)
    public List<User> getAllUsers() {
        log.info("Получил запрос на получение всех  пользователей");
        return userService.getAllUsers();
    }

    @GetMapping(path + "/{id}")
    public User getUser(@PathVariable Integer id) {
        log.info("Получил запрос на получение пользователя по его id {}", id);
        return userService.getUserById(id);
    }

    @PutMapping(path + "/{id}/friends/{friend-id}")
    public void addFriend(@PathVariable Integer id, @PathVariable("friend-id") Integer friendId) {
        log.info("Получил запрос на добавление в друзья к пользователю c id {}  от пользователя с id{}", id, friendId);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping(path + "/{id}/friends/{friend-id}")
    public void deleteFriend(@PathVariable Integer id, @PathVariable("friend-id") Integer friendId) {
        log.info("Получил запрос на удаление из друзей у пользователя c id {}  пользователя с id{}", id, friendId);
        userService.deleteFriend(id, friendId);
    }

    @DeleteMapping(path + "/{id}")
    public void deleteUser(@PathVariable Integer id) {
        log.info("Получил запрос на удаление пользователя с id {}", id);
        userService.deleteUserById(id);
    }

    @GetMapping(path + "/{id}/friends")
    public List<User> getFriends(@PathVariable Integer id) {
        log.info("Получил запрос на получение списка друзей у пользователя с id {}", id);
        return userService.getAllUserFriends(id);
    }

    @GetMapping(path + "/{id}/friends/common/{other-id}")
    public List<User> getMutualFriends(@PathVariable Integer id, @PathVariable("other-id") Integer otherId) {
        log.info("Получил запрос на получение общих друзей у пользователя с id {} и пользователя с id {}", id, otherId);
        return userService.getMutualFriends(id, otherId);
    }

    @GetMapping(path + "/{id}/feed")
    public List<Event> getUserFeed(@PathVariable Integer id) {
        log.info("Запрос на получение списка событий пользователя с id {}", id);
        List<Event> userFeed = userService.getUserFeed(id);
        try {
            log.info("Получил список событий пользователя с id {} - {},", id, objectMapper.writeValueAsString(userFeed));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return userFeed;
    }

    @GetMapping(path + "/{id}/recommendations")
    public List<Film> getUsersRecommendations(@PathVariable Integer id) {
        log.info("Получил запрос на получение фильмов по рекомендации пользователя с id {} ", id);
        List<Film> usersRecommendations = userService.getUsersRecommendations(id);
        try {
            log.info("Получил список фильмов по рекомендации:{}", objectMapper.writeValueAsString(usersRecommendations));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return usersRecommendations;
    }
}