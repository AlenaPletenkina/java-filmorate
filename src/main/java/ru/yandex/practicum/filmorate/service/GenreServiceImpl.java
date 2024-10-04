package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

import static java.util.Objects.isNull;

@Service
@Slf4j
public class GenreServiceImpl implements GenreService {
    private final GenreDao genreDbStorage;

    @Autowired
    public GenreServiceImpl(GenreDao genreDbStorage) {
        this.genreDbStorage = genreDbStorage;
    }

    @Override
    public List<Genre> getGenres() {
        return genreDbStorage.getGenres();
    }

    @Override
    public Genre getGenre(Integer id) {
        Genre genre = genreDbStorage.getGenre(id);
        if (isNull(genre)) {
            throw new NotFoundException("Жанра с таким id не существует");
        }
        return genre;
    }

    @Override
    public void setGenre(Integer idFilm, Integer idGenre) {
        genreDbStorage.setGenres(idFilm, idGenre);
        log.info("Добавил жанр {} к фильму {}", idGenre, idFilm);
    }

    @Override
    public List<Genre> getFilmGenres(Integer filmId) {
        log.info("Приступаю к поиску жанров для фильма {}", filmId);
        List<Genre> filmGenres = genreDbStorage.getFilmGenres(filmId);
        log.info("Нашел {} жанров к фильму", filmGenres.size());
        return filmGenres;
    }

    @Override
    public void clearFilmGenres(Integer filmId) {
        genreDbStorage.clearFilmGenres(filmId);
    }
}