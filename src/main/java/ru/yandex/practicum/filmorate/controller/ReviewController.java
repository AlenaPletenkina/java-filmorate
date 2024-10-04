package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;
    private final ObjectMapper objectMapper;

    @Autowired
    public ReviewController(ReviewService reviewService, ObjectMapper objectMapper) {
        this.reviewService = reviewService;
        this.objectMapper = objectMapper;
    }

    @PostMapping
    public Review createReview(@RequestBody Review review) {
        log.info("Получил запрос на создание отзыва {}", review);
        Review createdReview = reviewService.createReview(review);
        try {
            log.info("Отзыв успешно создан, response:{}", objectMapper.writeValueAsString(createdReview));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return createdReview;
    }

    @GetMapping
    public List<Review> getAllReviewByFilmId(
            @RequestParam(required = false) Integer filmId,
            @RequestParam(required = false) Integer count) {
        log.info("Получил запрос на получение всех отзывов фильма с Id {}", filmId);
        return reviewService.getReview(filmId, count);
    }

    @GetMapping("/{reviewId}")
    public Review getReviewById(@PathVariable Integer reviewId) {
        log.info("Получил запрос на получение отзыва по его Id {}", reviewId);
        return reviewService.getReviewById(reviewId);
    }

    @PutMapping
    public Review updateReview(@RequestBody Review review) {
        log.info("Получил запрос на обновление отзыва {}", review);
        return reviewService.updateReview(review);
    }

    @DeleteMapping("/{reviewId}")
    public void deleteReviewById(@PathVariable("reviewId") Integer reviewId) {
        log.info("Получил запрос на удаление  отзыва по его Id {}", reviewId);
        reviewService.deleteReviewById(reviewId);
    }

    @PutMapping("{reviewId}/like/{userId}")
    public void likeReview(@PathVariable Integer reviewId, @PathVariable Integer userId) {
        log.info("Получил запрос на постановку лайка для отзыва с Id {}  от пользователя с Id {}", reviewId, userId);
        reviewService.likeReview(reviewId, userId);
    }

    @PutMapping("{reviewId}/dislike/{userId}")
    public void dislikeReview(@PathVariable Integer reviewId, @PathVariable Integer userId) {
        log.info("Получил запрос на постановку дизлайка для отзыва с Id {}  от пользователя с Id {}", reviewId, userId);
        reviewService.dislikeReview(reviewId, userId);
    }

    @DeleteMapping("{reviewId}/like/{userId}")
    public void deleteLikeReview(@PathVariable Integer reviewId, @PathVariable Integer userId) {
        log.info("Получил запрос на удаление лайка для отзыва с Id {}  от пользователя с Id {}", reviewId, userId);
        reviewService.deleteLikeReview(reviewId, userId);
    }

    @DeleteMapping("{reviewId}/dislike/{userId}")
    public void deleteDislikeReview(@PathVariable Integer reviewId, @PathVariable Integer userId) {
        log.info("Получил запрос на удаление дизлайка для отзыва с Id {}  от пользователя с Id {}", reviewId, userId);
        reviewService.deleteDislikeReview(reviewId, userId);
    }
}