package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.event.EventStorage;
import ru.yandex.practicum.filmorate.dao.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.impl.UserDbStorage;
import ru.yandex.practicum.filmorate.dao.review.ReviewDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

import static java.util.Objects.isNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;
    private final ReviewDbStorage reviewDbStorage;
    private final EventStorage eventStorage;

    private final Integer defaultCountReview = 10;

    public Review createReview(Review review) {
        validate(review);
        checkingForExistenceUser(review.getUserId());
        checkingForExistenceFilm(review.getFilmId());
        Review rew = reviewDbStorage.createReview(review);
        eventStorage.add(new Event(rew.getUserId(), EventType.REVIEW, rew.getReviewId(), Operation.ADD));
        log.info("Добавляем пользователю {} событие {} - {} с фильмом {}", rew.getUserId(), EventType.REVIEW, Operation.ADD, rew.getFilmId());
        return rew;
    }

    public List<Review> getReview(Integer filmId, Integer count) {
        if (count == null || count == 0) {
            count = defaultCountReview;
        }
        if (filmId == null) {
            log.info("Получение списка из {} отзывов:", count);
            return reviewDbStorage.getReview(count);
        } else {
            checkingForExistenceFilm(filmId);
            log.info("Получение списка из {} отзывов для фильма с ИД: {}", count, filmId);
            return reviewDbStorage.getReviewByFilmId(filmId, count);
        }
    }

    public Review getReviewById(Integer reviewId) {
        checkingForExistenceReview(reviewId);
        log.info("Получение отзыва по ИД: {}", reviewId);
        return reviewDbStorage.getReviewById(reviewId);
    }

    public Review updateReview(Review review) {
        validate(review);
        checkingForExistenceReview(review.getReviewId());
        log.info("Редактирование уже имеющегося отзыва: {}", review);
        Review rew = reviewDbStorage.updateReview(review);
        eventStorage.add(new Event(rew.getUserId(), EventType.REVIEW, rew.getReviewId(), Operation.UPDATE));
        log.info("Добавляем пользователю {} событие {} - {} с фильмом {}", rew.getUserId(), EventType.REVIEW, Operation.UPDATE, rew.getFilmId());
        return rew;
    }

    public void deleteReviewById(Integer reviewId) {
        checkingForExistenceReview(reviewId);
        log.info("Удаление уже имеющегося отзыва c ИД: {}", reviewId);
        Review rew = getReviewById(reviewId);
        eventStorage.add(new Event(rew.getUserId(), EventType.REVIEW, rew.getReviewId(), Operation.REMOVE));
        log.info("Добавляем пользователю {} событие {} - {} с фильмом {}", rew.getUserId(), EventType.REVIEW, Operation.REMOVE, rew.getFilmId());
        reviewDbStorage.deleteReviewById(reviewId);
    }

    public void likeReview(Integer reviewId, Integer userId) {
        checkingForExistenceReview(reviewId);
        checkingForExistenceUser(userId);
        log.info("Пользователь: {} ставит лайк отзыву: {}", userId, reviewId);
        reviewDbStorage.likeReview(reviewId, userId);
    }

    public void dislikeReview(Integer reviewId, Integer userId) {
        checkingForExistenceReview(reviewId);
        checkingForExistenceUser(userId);
        log.info("Пользователь: {} ставит дизлайк отзыву: {}", userId, reviewId);
        reviewDbStorage.dislikeReview(reviewId, userId);
    }

    public void deleteLikeReview(Integer reviewId, Integer userId) {
        checkingForExistenceReview(reviewId);
        checkingForExistenceUser(userId);
        log.info("Пользователь: {} удаляет лайк отзыву: {}", userId, reviewId);
        reviewDbStorage.deleteLikeReview(reviewId, userId);
    }

    public void deleteDislikeReview(Integer reviewId, Integer userId) {
        checkingForExistenceReview(reviewId);
        checkingForExistenceUser(userId);
        log.info("Пользователь: {} удаляет дизлайк отзыву: {}", userId, reviewId);
        reviewDbStorage.deleteDislikeReview(reviewId, userId);
    }

    private void checkingForExistenceReview(Integer reviewId) {
        if (!reviewDbStorage.contains(reviewId)) {
            throw new NotFoundException("Отзыв с id " + reviewId + " не найден.");
        }
    }

    private void checkingForExistenceFilm(Integer filmId) {
        if (!filmDbStorage.contains(filmId)) {
            throw new NotFoundException("Фильм с id " + filmId + " не найден.");
        }
    }

    private void checkingForExistenceUser(Integer userId) {
        if (!userDbStorage.contains(userId)) {

            throw new NotFoundException("Пользователь с id " + userId + " не найден.");
        }
    }

    private void validate(Review review) {
        if (isNull(review.getContent()) || review.getContent().isEmpty()) {
            log.error("Пустой отзыв{}", review);
            throw new ValidationException("Отзыв не может быть пустым");
        }
        if (isNull(review.getIsPositive())) {
            log.error("Пустой рейтинг");
            throw new ValidationException("Рейтинг не может быть пустым");
        }
        if (isNull(review.getUserId())) {
            log.error("Id пользователя не может быть пустым");
            throw new ValidationException("Id пользователя не может быть пустым");
        }
        if (isNull(review.getFilmId())) {
            log.error("Id фильма не может быть пустым");
            throw new ValidationException("Id фильма не может быть пустым");
        }
    }
}