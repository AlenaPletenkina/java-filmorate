package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private static int count = 1;

    @Override
    public Film addFilm(Film film) {
        film.setId(generateId());
        films.put(film.getId(), film);
        log.info("Добавлен фильм: {}", film);
        return films.get(film.getId());
    }

    @Override
    public Film updateFilm(Film film) {
        Film filmToUpdate = films.get(film.getId());
        if (isNull(filmToUpdate)) {
            throw new NotFoundException("Фильм с id " + film.getId() + " не найден");
        }
        update(film, filmToUpdate);
        log.info("Обновлён фильм: {}", filmToUpdate);
        return filmToUpdate;
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilm(Integer id) {
        Film film = films.get(id);
        if (isNull(film)) {
            throw new NotFoundException("Фильм с id " + id + " не найден");
        }
        return film;
    }

    @Override
    public void deleteFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new ValidationException("Фильм " + film + " не может быть удалён, так как не существует ");
        }
        films.remove(film.getId());
        log.info("Удалён фильм: {}", film);
    }

    @Override
    public List<Integer> getFilmsUserById(Integer userId) {
        return films.values().stream()
                .filter(film -> film.getLikes() != null && film.getLikes() > 0)
                .map(Film::getId)
                .collect(Collectors.toList());
    }

    private void update(Film film, Film filmToUpdate) {
        filmToUpdate.setName(film.getName());
        filmToUpdate.setDescription(film.getDescription());
        filmToUpdate.setDuration(film.getDuration());
        filmToUpdate.setReleaseDate(film.getReleaseDate());
        filmToUpdate.setMpa(film.getMpa());
        filmToUpdate.setGenres(new LinkedHashSet<>(film.getGenres()));
        filmToUpdate.setDirectors(new LinkedHashSet<>(film.getDirectors()));
        // Предполагается, что лайки обрабатываются отдельно через LikeDbStorage
    }

    @Override
    public void deleteFilmById(Integer id) {
        if (!films.containsKey(id)) {
            throw new ValidationException("Фильм с id " + id + " не существует");
        }
        films.remove(id);
        log.info("Удалён фильм с id: {}", id);
    }

    @Override
    public List<Film> getTopFilmsWithFilters(Integer genreId, Integer year, Integer count) {
        return films.values().stream()
                .filter(film -> {
                    if (genreId != null) {
                        return film.getGenres() != null && film.getGenres().stream()
                                .anyMatch(genre -> genre.getId().equals(genreId));
                    }
                    return true;
                })
                .filter(film -> {
                    if (year != null) {
                        return film.getReleaseDate().getYear() == year;
                    }
                    return true;
                })
                .sorted((f1, f2) -> {
                    return Integer.compare(f2.getLikes(), f1.getLikes());
                })
                .toList();
    }


    @Override
    public List<Film> getSortedDirectorsFilmsByYears(long id) {
        return List.of();
    }

    @Override
    public List<Film> getSortedDirectorsFilmsByLikes(long id) {
        return List.of();
    }

    @Override
    public List<Film> searchFilmsByTitle(String query) {
        return films.values().stream()
                .filter(film -> film.getName().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Film> searchFilmsByDirector(String query) {
        return films.values().stream()
                .filter(film -> film.getDirectors().stream()
                        .anyMatch(director -> director.getName().toLowerCase().contains(query.toLowerCase())))
                .collect(Collectors.toList());
    }

    private int generateId() {
        return count++;
    }
}