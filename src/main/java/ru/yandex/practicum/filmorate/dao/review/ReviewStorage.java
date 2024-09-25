package ru.yandex.practicum.filmorate.dao.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {
    // Добавление нового отзыва
    Review createReview(Review review);

    // Получение определённого количество отзывов
    List<Review> getReview(Integer count);

    // Получение отзыва по идентификатору
    Review getReviewById(Integer reviewId);

    // Получение определённое количество отзывов по идентификатору фильма
    List<Review> getReviewByFilmId(Integer filmId, Integer count);

    // Редактирование уже имеющегося отзыва
    Review updateReview(Review review);

    // Удаление уже имеющегося отзыва по идентификатору
    void deleteReviewById(Integer reviewId);

    // Пользователь ставит лайк отзыву
    void likeReview(Integer reviewId, Integer userId);

    // Пользователь ставит дизлайк отзыву
    void dislikeReview(Integer reviewId, Integer userId);

    // Пользователь удаляет лайк отзыву
    void deleteLikeReview(Integer reviewId, Integer userId);

    // Пользователь удаляет дизлайк отзыву
    void deleteDislikeReview(Integer reviewId, Integer userId);

    // Проверка на существование отзыва
    boolean contains(Integer reviewId);
}
