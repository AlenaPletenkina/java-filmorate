package ru.yandex.practicum.filmorate.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.like.LikeDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
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

    @Autowired
    public FilmServiceImpl(@Qualifier("H2FilmDb") FilmStorage filmStorage,
                           ObjectMapper objectMapper, @Qualifier("H2UserDb") UserStorage userStorage,
                           LikeDbStorage likeDbStorage, GenreService genreService) {
        this.filmStorage = filmStorage;
        this.objectMapper = objectMapper;
        this.userStorage = userStorage;
        this.likeDbStorage = likeDbStorage;
        this.genreService = genreService;
    }

    @Override
    public void addLike(Integer filmId, Integer userId) {
        validateUserId(userId);
        likeDbStorage.addLike(filmId, userId);
    }

    @Override
    public void deleteLike(Integer filmId, Integer userId) {
        validateUserId(userId);
        likeDbStorage.deleteLike(filmId, userId);
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        return filmStorage.getAllFilms().stream()
                .filter(f -> nonNull(f.getLikes()))
                .sorted((f1, f2) -> f2.getLikes() - f1.getLikes())
                .limit(count)
                .collect(Collectors.toList());
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
        }
        return updateFilm;
    }

    @Override
    public List<Film> getAllFilms() {
        List<Film> allFilms = filmStorage.getAllFilms();
        for (Film film : allFilms) {
            List<Genre> filmGenres = genreService.getFilmGenres(film.getId());
            film.setGenres(filmGenres);
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

        // Удаляем лайки, связанные с фильмом
        likeDbStorage.deleteLikesByFilmId(id);
        log.info("Лайки удалены для фильма с id: {}", id);

        // Удаляем жанры фильма
        genreService.clearFilmGenres(id);
        log.info("Жанры удалены для фильма с id: {}", id);

        // Удаляем сам фильм
        filmStorage.deleteFilmById(id);
        log.info("Фильм с id: {} успешно удалён", id);
    }

    @Override
    public List<Film> getCommonFilms(Integer userId, Integer friendId) {
        List<Integer> userFilms = filmStorage.getFilmsUserById(userId);
        List<Integer> friendFilms = filmStorage.getFilmsUserById(friendId);

        // Находим пересечение списков фильмов пользователей
        Set<Integer> commonFilmIds = userFilms.stream()
                .filter(friendFilms::contains)
                .collect(Collectors.toSet());

        // Получаем сами фильмы по ID и сортируем по числу лайков
        return commonFilmIds.stream()
                .map(filmStorage::getFilm) // Получаем фильм по его id
                .filter(film -> nonNull(film.getLikes())) // Фильтруем фильмы с лайками
                .sorted((f1, f2) -> f2.getLikes() - f1.getLikes()) // Сортируем по популярности
                .collect(Collectors.toList());
    }

    private void validateUserId(Integer id) {
        userStorage.getUserById(id);
    }
}
