package ru.yandex.practicum.filmorate.dao.like;

public interface LikeStorage {
    void addLike(Integer filmId, Integer userId);

    void deleteLike(Integer filmId, Integer userId);

    void deleteLikesByFilmId(Integer filmId);

}