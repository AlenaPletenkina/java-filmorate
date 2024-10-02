package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {
    void addLike(Integer filmId, Integer userId);

    void deleteLike(Integer filmId, Integer userId);

    List<Film> getPopularFilms(Integer count);

    Film addFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getAllFilms();

    Film getFilm(Integer id);

    void deleteFilm(Film film);

    void deleteFilmById(Integer id);

    List<Film> getTopFilmsWithFilters(Integer genreId, Integer year, Integer count);

    List<Film> getCommonFilms(Integer userId, Integer friendId);

    List<Film> getSortedDirectorsFilms(Integer id, String sortBy);

    List<Film> searchFilms(String query, String by);
}