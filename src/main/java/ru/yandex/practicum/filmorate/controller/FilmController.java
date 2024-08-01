package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

@Slf4j
@RestController
public class FilmController {
    Map<Integer, Film> films = new HashMap<>();
    public static int count = 1;
    private final String path = "/films";

    @PostMapping(path)
    public Film addFilm(@RequestBody Film film) {
        validate(film);
        film.setId(generateId());
        films.put(film.getId(), film);
        return films.get(film.getId());
    }

    @PutMapping(path)
    public Film updateFilm(@RequestBody Film film) {
        validate(film);
        Film filmToUpdate = films.get(film.getId());
        if (isNull(filmToUpdate)) {
            throw new NotFoundException("Фильм с id " + film.getId() + " не найден");
        }
        update(film, filmToUpdate);
        return filmToUpdate;
    }

    @GetMapping(path)
    public List<Film> getAllFilms() {
        Collection<Film> listOfFilms = films.values();
        return listOfFilms.stream().toList();
    }

    private void update(Film film, Film filmToUpdate) {
        filmToUpdate.setName(film.getName());
        filmToUpdate.setDescription(film.getDescription());
        filmToUpdate.setDuration(film.getDuration());
        filmToUpdate.setReleaseDate(film.getReleaseDate());
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

    private int generateId() {
        return count++;
    }
}
