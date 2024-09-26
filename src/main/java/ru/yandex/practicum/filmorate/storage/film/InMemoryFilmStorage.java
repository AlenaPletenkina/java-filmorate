package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;


@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    public static int count = 1;

    @Override
    public Film addFilm(Film film) {
        film.setId(generateId());
        films.put(film.getId(), film);
        return films.get(film.getId());
    }

    @Override
    public Film updateFilm(Film film) {
        Film filmToUpdate = films.get(film.getId());
        if (isNull(filmToUpdate)) {
            throw new NotFoundException("Фильм с id " + film.getId() + " не найден");
        }
        update(film, filmToUpdate);
        return filmToUpdate;
    }

    @Override
    public List<Film> getAllFilms() {
        Collection<Film> listOfFilms = films.values();
        return listOfFilms.stream().toList();
    }

    @Override
    public Film getFilm(Integer id) {
        if (!films.containsKey(id)) {
            throw new NotFoundException("Фильм с id " + id + " не найден");
        }
        return films.get(id);
    }

    @Override
    public void deleteFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new ValidationException("Фильм " + film + " не может быть удален, так как не существует ");
        }
        films.remove(film.getId());
    }

    @Override
    public List<Integer> getFilmsUserById(Integer userId) {
        // Here you can implement custom filtering logic based on the number of likes or other conditions
        return films.values().stream()
                .filter(film -> film.getLikes() != null && film.getLikes() > 0) // Example: Filter films with more than 0 likes
                .map(Film::getId)
                .collect(Collectors.toList());
    }



    private void update(Film film, Film filmToUpdate) {
        filmToUpdate.setName(film.getName());
        filmToUpdate.setDescription(film.getDescription());
        filmToUpdate.setDuration(film.getDuration());
        filmToUpdate.setReleaseDate(film.getReleaseDate());
    }

    @Override
    public void deleteFilmById(Integer id) {
        if (!films.containsKey(id)) {
            throw new ValidationException("Фильм с id " + id + " не существует");
        }
        films.remove(id);
    }

    private int generateId() {
        return count++;
    }
}
