package ru.yandex.practicum.filmorate.dao.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.List;

public interface DirectorStorage {

    Director save(Director director);

    Director update(Director director);

    Collection<Director> findAll();

    Director findDirectorById(Integer id);

    boolean deleteDirector(Integer id);

    void setDirectors(Integer idFilm, Integer idDirector);

    List<Director> getFilmDirectors(Integer filmId);

    void clearFilmDirectors(Integer filmId);
}