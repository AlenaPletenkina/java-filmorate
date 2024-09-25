package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;


@RestController
@Slf4j
        //@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;
    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // Добавление нового отзыва
    @PostMapping
    public Review createReview(@RequestBody Review review) {
        log.info("Получил запрос на создание отзыва {}", review);
        return reviewService.createReview(review);
    }

    // Получение всех отзывов по идентификатору фильма, если фильм не указан то все. Если кол-во не указано, то 10.
    @GetMapping
    public List<Review> getAllReviewByFilmId(
            @RequestParam(required = false) Integer filmId,
            @RequestParam(required = false) Integer count) {
        return reviewService.getReview(filmId, count);
    }

    // Получение отзыва по идентификатору
    @GetMapping("/{reviewId}")
    public Review getReviewById(@PathVariable Integer reviewId) {
        return reviewService.getReviewById(reviewId);
    }

    // Редактирование уже имеющегося отзыва
    @PutMapping
    public Review updateReview(@RequestBody Review review) {
        return reviewService.updateReview(review);
    }

    // Удаление уже имеющегося отзыва по идентификатору
    @DeleteMapping("/{reviewId}")
    public void deleteReviewById(@PathVariable("reviewId") Integer reviewId) {
        reviewService.deleteReviewById(reviewId);
    }

    // Пользователь ставит лайк отзыву
    @PutMapping("{reviewId}/like/{userId}")
    public void likeReview(@PathVariable Integer reviewId, @PathVariable Integer userId) {
        reviewService.likeReview(reviewId, userId);
    }

    // Пользователь ставит дизлайк отзыву
    @PutMapping("{reviewId}/dislike/{userId}")
    public void dislikeReview(@PathVariable Integer reviewId, @PathVariable Integer userId) {
        reviewService.dislikeReview(reviewId, userId);
    }

    // Пользователь удаляет лайк отзыву
    @DeleteMapping("{reviewId}/like/{userId}")
    public void deleteLikeReview(@PathVariable Integer reviewId, @PathVariable Integer userId) {
        reviewService.deleteLikeReview(reviewId, userId);
    }

    // Пользователь удаляет дизлайк отзыву
    @DeleteMapping("{reviewId}/dislike/{userId}")
    public void deleteDislikeReview(@PathVariable Integer reviewId, @PathVariable Integer userId) {
        reviewService.deleteDislikeReview(reviewId, userId);
    }
}