package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.RatingService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@Slf4j
public class RatingController {
    private final RatingService ratingService;

    @Autowired
    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @GetMapping
    public List<Rating> getAllRating() {
        log.info("Получил запрос на получение всех рейтингов");
        return ratingService.getAllRating();
    }

    @GetMapping("/{id}")
    public Rating getRatingByID(@PathVariable Integer id) {
        log.info("Получил запрос на получение рейтинга по его id {}",id);
        return ratingService.getRatingByID(id);
    }
}