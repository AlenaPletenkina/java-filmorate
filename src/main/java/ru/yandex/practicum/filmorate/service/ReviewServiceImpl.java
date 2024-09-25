package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.impl.UserDbStorage;
import ru.yandex.practicum.filmorate.dao.review.ReviewDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;
    private final ReviewDbStorage reviewDbStorage;

    private final Integer defaultCountReview = 10;

    // Добавление нового отзыва
    public Review createReview(Review review) {
        checkingForExistenceUser(review.getUserId());
        checkingForExistenceFilm(review.getFilmId());
        log.info("Создание отзыва: {}", review);
        return reviewDbStorage.createReview(review);
    }

    // Получение всех отзывов по идентификатору фильма, если фильм не указан, то все. Если кол-во не указано, то 10.
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


    // Получение отзыва по идентификатору
    public Review getReviewById(Integer reviewId) {
        checkingForExistenceReview(reviewId);
        log.info("Получение отзыва по ИД: {}", reviewId);
        return reviewDbStorage.getReviewById(reviewId);
    }

    // Редактирование уже имеющегося отзыва
    public Review updateReview(Review review) {
        checkingForExistenceReview(review.getReviewId());
        log.info("Редактирование уже имеющегося отзыва: {}", review);
        return reviewDbStorage.updateReview(review);
    }

    // Удаление уже имеющегося отзыва по идентификатору
    public void deleteReviewById(Integer reviewId) {
        checkingForExistenceReview(reviewId);
        log.info("Удаление уже имеющегося отзыва c ИД: {}", reviewId);
        reviewDbStorage.deleteReviewById(reviewId);
    }

    // Пользователь ставит лайк отзыву
    public void likeReview(Integer reviewId, Integer userId) {
        checkingForExistenceReview(reviewId);
        checkingForExistenceUser(userId);
        log.info("Пользователь: {} ставит лайк отзыву: {}", userId, reviewId);
        reviewDbStorage.likeReview(reviewId, userId);
    }

    // Пользователь ставит дизлайк отзыву
    public void dislikeReview(Integer reviewId, Integer userId) {
        checkingForExistenceReview(reviewId);
        checkingForExistenceUser(userId);
        log.info("Пользователь: {} ставит дизлайк отзыву: {}", userId, reviewId);
        reviewDbStorage.dislikeReview(reviewId, userId);
    }

    // Пользователь удаляет лайк отзыву
    public void deleteLikeReview(Integer reviewId, Integer userId) {
        checkingForExistenceReview(reviewId);
        checkingForExistenceUser(userId);
        log.info("Пользователь: {} удаляет лайк отзыву: {}", userId, reviewId);
        reviewDbStorage.deleteLikeReview(reviewId, userId);
    }

    // Пользователь удаляет дизлайк отзыву
    public void deleteDislikeReview(Integer reviewId, Integer userId) {
        checkingForExistenceReview(reviewId);
        checkingForExistenceUser(userId);
        log.info("Пользователь: {} удаляет дизлайк отзыву: {}", userId, reviewId);
        reviewDbStorage.deleteDislikeReview(reviewId, userId);
    }

    // проверка на существование отзыва по идентификатору
    private void checkingForExistenceReview(Integer reviewId) {
        if (!reviewDbStorage.contains(reviewId)) {
            throw new NotFoundException("Отзыв с id " + reviewId + " не найден.");
        }
    }

    // проверка на существование фильма по идентификатору
    private void checkingForExistenceFilm(Integer filmId) {
        if (!reviewDbStorage.contains(filmId)) {
            throw new NotFoundException("Фильм с id " + filmId + " не найден.");
        }
    }

    // проверка на существование пользователя по идентификатору
    private void checkingForExistenceUser(Integer userId) {
        if (!reviewDbStorage.contains(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден.");
        }
    }
}
