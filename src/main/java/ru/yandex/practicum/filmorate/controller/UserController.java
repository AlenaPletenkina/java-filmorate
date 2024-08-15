package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final String path = "/users";

    @Autowired
    private final UserService userService;

    @PostMapping(path)
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping(path)
    public User updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }

    @GetMapping(path)
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping(path + "/{id}")
    public User getUser(@PathVariable Integer id) {
        return userService.getUserById(id);
    }

    @PutMapping(path+"/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        userService.addFriend(id,friendId);
    }
    @DeleteMapping(path+"/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        userService.deleteFriend(id,friendId);
    }

    @GetMapping(path + "/{id}/friends")
    public List<User> getFriends(@PathVariable Integer id) {
        return userService.getAllUserFriends(id);
    }

    @GetMapping(path + "/{id}/friends/common/{otherId}")
    public List<User> getMutualFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
        return userService.getMutualFriends(id,otherId);
    }
}
