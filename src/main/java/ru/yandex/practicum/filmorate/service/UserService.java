package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {
    void addFriend(Integer user, Integer userFriend);

    void deleteFriend(Integer user, Integer userFriend);

    List<User> getMutualFriends(Integer user, Integer userFriend);

    User createUser(User user);

    User updateUser(User user);

    List<User> getAllUsers();

    User getUserById(Integer id);

    public void deleteUser(User user);

    void deleteUserById(Integer id);

    List<Event> getUserFeed(Integer id);

    List<User> getAllUserFriends(Integer id);

    List<Film> getUsersRecommendations(Integer id);

}