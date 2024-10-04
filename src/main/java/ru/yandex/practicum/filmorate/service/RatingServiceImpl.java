package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.RatingDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;

import static java.util.Objects.isNull;

@Service
@Slf4j
public class RatingServiceImpl implements RatingService {
    private final RatingDao ratingDbStorage;

    @Autowired
    public RatingServiceImpl(RatingDao ratingDbStorage) {
        this.ratingDbStorage = ratingDbStorage;
    }

    @Override
    public List<Rating> getAllRating() {
        return ratingDbStorage.getRatingList();
    }

    @Override
    public Rating getRatingByID(Integer id) {
        Rating rating = ratingDbStorage.getRating(id);
        if (isNull(rating)) {
            throw new NotFoundException("Рейтинга с таким id не существует");
        }
        return rating;
    }
}