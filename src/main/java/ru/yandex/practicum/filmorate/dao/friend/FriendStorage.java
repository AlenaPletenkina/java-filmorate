package ru.yandex.practicum.filmorate.dao.friend;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendStorage {
    void addFriend(Integer userIdOne, Integer userIdTwo);

    void deleteFriend(Integer userIdOne, Integer userIdTwo);

    Boolean isFriend(Integer userIdOne, Integer userIdTwo);

    List<User> getAllUserFriends(Integer userId);

    List<User> getMutualFriends(Integer userIdOne, Integer userIdTwo);

    void deleteAllFriendsForUser(Integer userId);
}