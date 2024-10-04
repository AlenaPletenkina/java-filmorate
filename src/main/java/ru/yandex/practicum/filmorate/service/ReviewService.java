package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewService {
    Review createReview(Review review);

    List<Review> getReview(Integer filmId, Integer count);

    Review getReviewById(Integer reviewId);

    Review updateReview(Review review);

    void deleteReviewById(Integer reviewId);

    void likeReview(Integer reviewId, Integer userId);

    void dislikeReview(Integer reviewId, Integer userId);

    void deleteLikeReview(Integer reviewId, Integer userId);

    void deleteDislikeReview(Integer reviewId, Integer userId);

}