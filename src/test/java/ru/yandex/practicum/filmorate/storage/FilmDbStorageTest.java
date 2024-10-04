package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.utill.UtilReader;

import java.time.LocalDate;
import java.util.LinkedHashSet;


@JdbcTest
@AutoConfigureTestDatabase
@Import(FilmDbStorage.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {

    private final FilmStorage filmStorage;
    private final JdbcTemplate jdbcTemplate;

    Film testFilm = new Film(null,
            "Красотка",
            "Про встречу бизнесмена и рыжей бестии",
            LocalDate.of(1994, 6, 24),
            220,
            0,
            new Rating(1, "G"),
            null,
            null);

    @BeforeEach
    void beforeEach() {
        jdbcTemplate.update(UtilReader.readString("src/test/resources/drop.sql"));
        jdbcTemplate.update(UtilReader.readString("src/main/resources/schema.sql"));
        jdbcTemplate.update(UtilReader.readString("src/test/resources/data.sql"));
    }

    @Test
    void getAllTest() {
        int[] idArray = filmStorage.getAllFilms()
                .stream().mapToInt(Film::getId).toArray();
        Assertions.assertArrayEquals(new int[]{1, 2, 3}, idArray);
    }

    @Test
    void getById() {
        Film film = new Film(1,
                "Собака-кусака",
                "Про самого отважного пса",
                LocalDate.of(1969, 4, 28),
                100,
                1,
                new Rating(3, "PG-13"),
                new LinkedHashSet<>(), // Устанавливаем пустое множество
                new LinkedHashSet<>()  // Устанавливаем пустое множество
        );
        Assertions.assertEquals(film, filmStorage.getFilm(1));
    }

    @Test
    void addTest() {
        testFilm.setId(4);
        testFilm.setLikes(0);
        testFilm.setGenres(new LinkedHashSet<>()); // Устанавливаем пустое множество
        testFilm.setDirectors(new LinkedHashSet<>()); // Устанавливаем пустое множество
        Assertions.assertEquals(testFilm, filmStorage.addFilm(testFilm));
    }


    @Test
    void updateTest() {
        testFilm.setId(1);
        testFilm.setLikes(1);
        testFilm.setGenres(new LinkedHashSet<>()); // Устанавливаем пустое множество
        testFilm.setDirectors(new LinkedHashSet<>()); // Устанавливаем пустое множество
        Assertions.assertEquals(testFilm, filmStorage.updateFilm(testFilm));
    }
}

