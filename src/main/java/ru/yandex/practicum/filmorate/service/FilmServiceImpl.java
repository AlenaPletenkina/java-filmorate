package ru.yandex.practicum.filmorate.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.event.EventStorage;
import ru.yandex.practicum.filmorate.dao.like.LikeDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
@Slf4j
public class FilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;
    private final ObjectMapper objectMapper;
    private final UserStorage userStorage;
    private final LikeDbStorage likeDbStorage;
    private final GenreService genreService;
    private final DirectorService directorService;
    private final EventStorage eventStorage;


    @Autowired
    public FilmServiceImpl(@Qualifier("H2FilmDb") FilmStorage filmStorage,
                           ObjectMapper objectMapper, @Qualifier("H2UserDb") UserStorage userStorage,
                           LikeDbStorage likeDbStorage, GenreService genreService, DirectorService directorService,
                           EventStorage eventStorage) {
        this.filmStorage = filmStorage;
        this.objectMapper = objectMapper;
        this.userStorage = userStorage;
        this.likeDbStorage = likeDbStorage;
        this.genreService = genreService;
        this.directorService = directorService;
        this.eventStorage = eventStorage;
    }

    @Override
    public void addLike(Integer filmId, Integer userId) {
        validateUserId(userId);
        validateFilmId(filmId);
        likeDbStorage.addLike(filmId, userId);
        log.info("Добавляем пользователю {} событие {} - {} с фильмом {}", userId, EventType.LIKE, Operation.ADD, filmId);
        eventStorage.add(new Event(userId, EventType.LIKE, filmId, Operation.ADD));
    }

    @Override
    public void deleteLike(Integer filmId, Integer userId) {

        validateUserId(userId);
        User user = userStorage.getUserById(userId);
        log.info("Добавляем пользователю {} событие {} - {} с фильмом {}", user, EventType.LIKE, Operation.REMOVE, filmId);
        eventStorage.add(new Event(userId, EventType.LIKE, filmId, Operation.REMOVE));
        likeDbStorage.deleteLike(filmId, userId);
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        List<Film> films = filmStorage.getAllFilms().stream()
                .filter(f -> nonNull(f.getLikes()))
                .sorted((f1, f2) -> f2.getLikes() - f1.getLikes())
                .collect(Collectors.toList());

        if (count != null) {
            return films.stream().limit(count).collect(Collectors.toList());
        } else {
            return films;
        }
    }

    @Override
    public Film addFilm(Film film) {
        validate(film);
        try {
            log.info("Приступаю к созданию фильма");
            Film addFilm = filmStorage.addFilm(film);
            List<Genre> genres1 = film.getGenres();
            if (nonNull(genres1)) {
                Set<Genre> genres = new HashSet<>(genres1);
                for (Genre genre : genres) {
                    genreService.setGenre(addFilm.getId(), genre.getId());
                }
                addFilm.setGenres(genreService.getFilmGenres(addFilm.getId()));
            }

            List<Director> directors1 = film.getDirectors();
            if (nonNull(directors1)) {
                Set<Director> directors = new HashSet<>(directors1);
                for (Director director : directors) {
                    directorService.setDirector(addFilm.getId(), director.getId());
                }
                addFilm.setDirectors(directorService.getFilmDirectors(addFilm.getId()));
            }

            log.info("Создание фильма прошло успешно {}", objectMapper.writeValueAsString(addFilm));
            return addFilm;
        } catch (Exception ex) {
            throw new ValidationException(ex.getMessage());
        }
    }

    @Override
    public Film updateFilm(Film film) {
        validate(film);
        Film updateFilm = filmStorage.updateFilm(film);
        if (isNull(updateFilm)) {
            throw new NotFoundException("Фильма с таким id не существует");
        }
        genreService.clearFilmGenres(film.getId());
        List<Genre> genres = film.getGenres();
        if (nonNull(genres)) {
            for (Genre genre : genres) {
                genreService.setGenre(film.getId(), genre.getId());
            }
            updateFilm.setGenres(genreService.getFilmGenres(updateFilm.getId()));
        }
        directorService.clearFilmDirectors(film.getId());
        List<Director> directors = film.getDirectors();
        if (nonNull(directors)) {
            for (Director director : directors) {
                directorService.setDirector(film.getId(), director.getId());
            }
            updateFilm.setDirectors(directorService.getFilmDirectors(updateFilm.getId()));
        }
        return updateFilm;
    }

    @Override
    public List<Film> getAllFilms() {
        List<Film> allFilms = filmStorage.getAllFilms();
        for (Film film : allFilms) {
            List<Genre> filmGenres = genreService.getFilmGenres(film.getId());
            film.setGenres(filmGenres);
            List<Director> filmDirectors = directorService.getFilmDirectors(film.getId());
            film.setDirectors(filmDirectors);
        }
        return allFilms;
    }

    @Override
    public Film getFilm(Integer id) {
        Film film = filmStorage.getFilm(id);
        if (isNull(film)) {
            throw new NotFoundException("Фильма с таким id не существует");
        }
        List<Genre> filmGenres = genreService.getFilmGenres(id);
        film.setGenres(filmGenres);
        List<Director> filmDirectors = directorService.getFilmDirectors(id);
        film.setDirectors(filmDirectors);
        return film;
    }

    @Override
    public void deleteFilm(Film film) {
        filmStorage.deleteFilm(film);
        genreService.clearFilmGenres(film.getId());
    }

    private void validate(Film film) {
        if (film.getName().isEmpty()) {
            log.error("Пустое название фильма {}", film);
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            log.error("Описание фильма больше 200 символов {}", film.getDescription());
            throw new ValidationException("Описание фильма не может превышать 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Дата релиза фильма ранее даты создания кинематографа {}", film.getReleaseDate());
            throw new ValidationException("Дата релиза фильма не может быть ранее, чем 28.12.1895");
        }
        if (film.getDuration() <= 0) {
            log.error("Продолжительность фильма меньше или равна нулю {}", film.getDuration());
            throw new ValidationException("Продолжительность фильма не может быть меньше или равна нулю");
        }
    }

    @Override
    public void deleteFilmById(Integer id) {
        Film film = filmStorage.getFilm(id);
        if (film == null) {
            throw new NotFoundException("Фильм с id " + id + " не найден");
        }

        likeDbStorage.deleteLikesByFilmId(id);
        log.info("Лайки удалены для фильма с id: {}", id);

        genreService.clearFilmGenres(id);
        log.info("Жанры удалены для фильма с id: {}", id);

        filmStorage.deleteFilmById(id);
        log.info("Фильм с id: {} успешно удалён", id);
    }

    @Override
    public List<Film> getTopFilmsWithFilters(Integer genreId, Integer year) {
        return filmStorage.getTopFilmsWithFilters(genreId, year);
    }

    public List<Film> getCommonFilms(Integer userId, Integer friendId) {
        List<Integer> userFilms = filmStorage.getFilmsUserById(userId);
        List<Integer> friendFilms = filmStorage.getFilmsUserById(friendId);

        Set<Integer> commonFilmIds = userFilms.stream()
                .filter(friendFilms::contains)
                .collect(Collectors.toSet());

        return commonFilmIds.stream()
                .map(this::getFilm)
                .filter(film -> nonNull(film.getLikes()))
                .sorted((f1, f2) -> f2.getLikes() - f1.getLikes())
                .collect(Collectors.toList());
    }

    private void validateUserId(Integer id) {
        log.info("Зашли в метод validateUserId с юзером с id {}", id);
        if (isNull(userStorage.getUserById(id))) {
            throw new NotFoundException("Пользователя с таким id не существует");
        }
        log.info("Проверка прошла успешно юзер {} - существует", userStorage.getUserById(id));
    }

    private void validateFilmId(Integer id) {
        log.info("Зашли в метод validateFilmId с фильмом с id {}", id);
        if (isNull(filmStorage.getFilm(id))) {
            throw new NotFoundException("Фильма с таким id не существует");
        }
        log.info("Проверка прошла успешно фильм {} - существует", filmStorage.getFilm(id));
    }

    @Override
    public List<Film> getSortedDirectorsFilms(Integer id, String sortBy) {
        directorService.findDirectorById(id);
        List<Film> sortFilms;

        log.info("Проверяем способ сортировки");
        switch (sortBy) {
            case "year":
                sortFilms = filmStorage.getSortedDirectorsFilmsByYears(id);
                for (Film film : sortFilms) {
                    List<Genre> filmGenres = genreService.getFilmGenres(film.getId());
                    film.setGenres(filmGenres);
                    List<Director> filmDirectors = directorService.getFilmDirectors(film.getId());
                    film.setDirectors(filmDirectors);
                }
                return sortFilms;
            case "likes":
                sortFilms = filmStorage.getSortedDirectorsFilmsByLikes(id);
                for (Film film : sortFilms) {
                    List<Genre> filmGenres = genreService.getFilmGenres(film.getId());
                    film.setGenres(filmGenres);
                    List<Director> filmDirectors = directorService.getFilmDirectors(film.getId());
                    film.setDirectors(filmDirectors);
                }
                return sortFilms;
            default:
                throw new ValidationException(String.format("Передан некорректный параметр сортировки: %s", sortBy));
        }
    }
}
