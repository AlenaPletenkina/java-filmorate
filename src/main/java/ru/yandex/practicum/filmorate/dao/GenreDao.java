package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Map;

public interface GenreDao {
    List<Genre> getFilmGenres(Integer filmId);

    abstract List<Map<Integer, Genre>> getAllFilmsGenres();

    void setGenres(Integer filmId, Integer genreId);

    List<Genre> getGenres();

    Genre getGenre(Integer genreId);

    void clearFilmGenres(Integer filmId);

    boolean contains(Integer genreId);
}