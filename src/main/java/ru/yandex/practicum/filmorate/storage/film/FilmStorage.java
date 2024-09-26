package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film addFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getAllFilms();

    Film getFilm(Integer id);

    void deleteFilm(Film film);

    void deleteFilmById(Integer id);

    List<Film> getTopFilmsWithFilters(Integer limit, Integer genreId, Integer year);
}
