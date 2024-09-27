package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.List;

public interface DirectorService {

    Director create(Director director);

    Director findDirectorById(Integer id);

    Director update(Director director);

    Collection<Director> findAll();

    void deleteDirectorByID(Integer id);

    void setDirector(Integer idFilm, Integer idDirector);

    List<Director> getFilmDirectors(Integer filmId);

    void clearFilmDirectors(Integer filmId);
}