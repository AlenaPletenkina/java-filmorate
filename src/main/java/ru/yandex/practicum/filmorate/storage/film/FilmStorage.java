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

    List<Film> getTopFilmsWithFilters(Integer count, Integer genreId, Integer year);

    List<Integer> getFilmsUserById(Integer userId);

    List<Film> getSortedDirectorsFilmsByYears(long id);

    List<Film> getSortedDirectorsFilmsByLikes(long id);

    List<Film> searchFilmsByTitle(String query);

    List<Film> searchFilmsByDirector(String query);
}