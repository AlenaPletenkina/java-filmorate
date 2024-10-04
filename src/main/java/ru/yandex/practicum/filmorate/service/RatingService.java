package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;

public interface RatingService {
    List<Rating> getAllRating();

    Rating getRatingByID(Integer id);
}