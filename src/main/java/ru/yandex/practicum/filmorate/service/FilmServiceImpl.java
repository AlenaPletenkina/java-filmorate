package ru.yandex.practicum.filmorate.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.like.LikeDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashSet;
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

    @Autowired
    public FilmServiceImpl(@Qualifier("H2FilmDb") FilmStorage filmStorage,
                           ObjectMapper objectMapper, @Qualifier("H2UserDb") UserStorage userStorage,
                           LikeDbStorage likeDbStorage, GenreService genreService, DirectorService directorService) {
        this.filmStorage = filmStorage;
        this.objectMapper = objectMapper;
        this.userStorage = userStorage;
        this.likeDbStorage = likeDbStorage;
        this.genreService = genreService;
        this.directorService = directorService;
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
        List<Film> films = filmStorage.getAllFilms().stream()
                .filter(f -> nonNull(f.getLikes()))
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
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
            List<Genre> genres1 = List.copyOf(film.getGenres());
            if (nonNull(genres1) && !genres1.isEmpty()) {
                Set<Genre> genres = new HashSet<>(genres1);
                for (Genre genre : genres) {
                    genreService.setGenre(addFilm.getId(), genre.getId());
                }
                addFilm.setGenres(new LinkedHashSet<>(genreService.getFilmGenres(addFilm.getId())));
            }

            List<Director> directors1 = List.copyOf(film.getDirectors());
            if (nonNull(directors1) && !directors1.isEmpty()) {
                Set<Director> directors = new HashSet<>(directors1);
                for (Director director : directors) {
                    directorService.setDirector(addFilm.getId(), director.getId());
                }
                addFilm.setDirectors(new LinkedHashSet<>(directorService.getFilmDirectors(addFilm.getId())));
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
        List<Genre> genres = List.copyOf(film.getGenres());
        if (nonNull(genres) && !genres.isEmpty()) {
            for (Genre genre : genres) {
                genreService.setGenre(film.getId(), genre.getId());
            }
            // Преобразование List<Genre> в Set<Genre>
            updateFilm.setGenres(new LinkedHashSet<>(genreService.getFilmGenres(updateFilm.getId())));
        }
        directorService.clearFilmDirectors(film.getId());
        List<Director> directors = List.copyOf(film.getDirectors());
        if (nonNull(directors) && !directors.isEmpty()) {
            for (Director director : directors) {
                directorService.setDirector(film.getId(), director.getId());
            }
            // Преобразование List<Director> в Set<Director>
            updateFilm.setDirectors(new LinkedHashSet<>(directorService.getFilmDirectors(updateFilm.getId())));
        }
        return updateFilm;
    }

    @Override
    public List<Film> getAllFilms() {
        List<Film> allFilms = filmStorage.getAllFilms();
        for (Film film : allFilms) {
            List<Genre> filmGenres = genreService.getFilmGenres(film.getId());
            film.setGenres(new LinkedHashSet<>(filmGenres));
            List<Director> filmDirectors = directorService.getFilmDirectors(film.getId());
            film.setDirectors(new LinkedHashSet<>(filmDirectors));
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
        film.setGenres(new LinkedHashSet<>(filmGenres));
        List<Director> filmDirectors = directorService.getFilmDirectors(id);
        film.setDirectors(new LinkedHashSet<>(filmDirectors));
        return film;
    }

    @Override
    public void deleteFilm(Film film) {
        filmStorage.deleteFilm(film);
        genreService.clearFilmGenres(film.getId());
    }

    private void validate(Film film) {
        if (film.getName() == null || film.getName().trim().isEmpty()) {
            log.error("Пустое название фильма {}", film);
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription() == null || film.getDescription().length() > 200) {
            log.error("Описание фильма больше 200 символов {}", film.getDescription());
            throw new ValidationException("Описание фильма не может превышать 200 символов");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Дата релиза фильма ранее даты создания кинематографа {}", film.getReleaseDate());
            throw new ValidationException("Дата релиза фильма не может быть ранее, чем 28.12.1895");
        }
        if (film.getDuration() == null || film.getDuration() <= 0) {
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

        directorService.clearFilmDirectors(id);
        log.info("Режиссёры удалены для фильма с id: {}", id);

        filmStorage.deleteFilmById(id);
        log.info("Фильм с id: {} успешно удалён", id);
    }

    @Override
    public List<Film> getTopFilmsWithFilters(Integer genreId, Integer year) {
        List<Film> films = filmStorage.getTopFilmsWithFilters(genreId, year);
        for (Film film : films) {
            List<Genre> filmGenres = genreService.getFilmGenres(film.getId());
            film.setGenres(new LinkedHashSet<>(filmGenres));
            List<Director> filmDirectors = directorService.getFilmDirectors(film.getId());
            film.setDirectors(new LinkedHashSet<>(filmDirectors));
        }
        return films;
    }

    @Override
    public List<Film> getCommonFilms(Integer userId, Integer friendId) {
        List<Long> userFilms = filmStorage.getFilmsUserById(userId).stream()
                .map(Integer::longValue)
                .collect(Collectors.toList());
        List<Long> friendFilms = filmStorage.getFilmsUserById(friendId).stream()
                .map(Integer::longValue)
                .collect(Collectors.toList());

        Set<Long> commonFilmIds = userFilms.stream()
                .filter(friendFilms::contains)
                .collect(Collectors.toSet());

        return commonFilmIds.stream()
                .map(id -> getFilm(id.intValue()))
                .filter(film -> nonNull(film.getLikes()))
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .collect(Collectors.toList());
    }

    private void validateUserId(Integer id) {
        if (userStorage.getUserById(id) == null) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
    }

    @Override
    public List<Film> getSortedDirectorsFilms(Integer id, String sortBy) {
        directorService.findDirectorById(id);
        List<Film> sortFilms;

        log.info("Проверяем способ сортировки");
        switch (sortBy) {
            case "year":
                sortFilms = filmStorage.getSortedDirectorsFilmsByYears(id);
                break;
            case "likes":
                sortFilms = filmStorage.getSortedDirectorsFilmsByLikes(id);
                break;
            default:
                throw new ValidationException(String.format("Передан некорректный параметр сортировки: %s", sortBy));
        }

        for (Film film : sortFilms) {
            List<Genre> filmGenres = genreService.getFilmGenres(film.getId());
            film.setGenres(new LinkedHashSet<>(filmGenres));
            List<Director> filmDirectors = directorService.getFilmDirectors(film.getId());
            film.setDirectors(new LinkedHashSet<>(filmDirectors));
        }

        return sortFilms;
    }

    @Override
    public List<Film> searchFilms(String query, String by) {
        Set<Film> result = new HashSet<>();
        String[] searchBy = by.split(",");

        for (String searchField : searchBy) {
            switch (searchField.trim()) {
                case "title":
                    result.addAll(filmStorage.searchFilmsByTitle(query));
                    break;
                case "director":
                    result.addAll(filmStorage.searchFilmsByDirector(query));
                    break;
                default:
                    throw new ValidationException("Некорректный параметр поиска: " + searchField);
            }
        }

        return result.stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .collect(Collectors.toList());
    }
}