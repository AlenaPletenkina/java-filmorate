package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper objectMapper;

    @Autowired
    public FilmController(FilmService filmService, ObjectMapper objectMapper) {
        this.filmService = filmService;
        this.objectMapper = objectMapper;
    }

    @PostMapping(path)
    public Film addFilm(@RequestBody Film film) {
        log.info("Получил запрос на создание фильма {}", film);
        Film createdFilm = filmService.addFilm(film);
        try {
            log.info("Фильм успешно создан, response:{}", objectMapper.writeValueAsString(createdFilm));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return createdFilm;
    }

    @PutMapping(path)
    public Film updateFilm(@RequestBody Film film) {
        log.info("Получил запрос на обновление фильма {}", film);
        return filmService.updateFilm(film);
    }

    @GetMapping(path)
    public List<Film> getAllFilms() {
        log.info("Получил запрос на получение всех фильмов");
        return filmService.getAllFilms();
    }

    @PutMapping(path + "/{film-id}/like/{user-id}")
    public void addLike(@PathVariable("film-id") Integer filmId, @PathVariable("user-id") Integer userId) {
        log.info("Получил запрос на добавление лайка к фильму с id = {}, от пользователя с id = {}", filmId, userId);
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping(path + "/{film-id}/like/{user-id}")
    public void deleteLike(@PathVariable("film-id") Integer filmId, @PathVariable("user-id") Integer userId) {
        log.info("Получил запрос на удаление лайка к фильму с id = {}, от пользователя с id = {}", filmId, userId);
        filmService.deleteLike(filmId, userId);
    }

    @GetMapping(path + "/popular")
    public List<Film> getPopularFilms(@RequestParam(required = false) Integer count) {
        log.info("Получил запрос на получение популярных фильмов, количество = {}", count);
        return filmService.getPopularFilms(count);
    }

    @GetMapping(path + "/{film-id}")
    public Film getFilmById(@PathVariable("film-id") Integer filmId) {
        log.info("Получил запрос на получение фильма по его id = {}", filmId);
        return filmService.getFilm(filmId);
    }

    @DeleteMapping(path + "/{id}")
    public void deleteFilm(@PathVariable Integer id) {
        log.info("Получил запрос на удаление фильма с id {}", id);
        filmService.deleteFilmById(id);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilmsWithFilters(
            @RequestParam(required = false) Integer genreId,
            @RequestParam(required = false) Integer year) {
        return filmService.getTopFilmsWithFilters(genreId, year);
    }

    @GetMapping(path + "/common")
    public List<Film> getCommonFilms(@RequestParam("userId") Integer userId, @RequestParam("friendId") Integer friendId) {
        log.info("Получил запрос на получение общих фильмов для пользователей {} и {}", userId, friendId);
        return filmService.getCommonFilms(userId, friendId);
    }

    @GetMapping(path + "/director/{directorId}")
    public List<Film> getByDirectorId(@PathVariable Integer directorId,
                                      @RequestParam String sortBy) {
        return filmService.getSortedDirectorsFilms(directorId, sortBy);
    }
}