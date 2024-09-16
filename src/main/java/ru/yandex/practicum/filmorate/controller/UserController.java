package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@Slf4j
@RestController
public class UserController {
    private final String path = "/users";
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(path)
    public User createUser(@RequestBody User user) {
        log.info("Получил запрос на создание пользователя {}", user);
        return userService.createUser(user);
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
}
