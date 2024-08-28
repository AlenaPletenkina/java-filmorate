package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@Slf4j
@RestController
public class FilmController {
    private final String path = "/films";
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping(path)
    public Film addFilm(@RequestBody Film film) {
        return filmService.addFilm(film);
    }

    @PutMapping(path)
    public Film updateFilm(@RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @GetMapping(path)
    public List<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    @PutMapping(path + "/{film-id}/like/{user-id}")
    public void addLike(@PathVariable("film-id") Integer filmId, @PathVariable("user-id") Integer userId) {
        filmService.addLike(filmId,userId);
    }

    @DeleteMapping(path + "/{film-id}/like/{user-id}")
    public void deleteLike(@PathVariable("film-id") Integer filmId, @PathVariable("user-id") Integer userId) {
        filmService.deleteLike(filmId, userId);
    }

    @GetMapping(path + "/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") Integer count) {
        return filmService.getPopularFilms(count);
    }
}
