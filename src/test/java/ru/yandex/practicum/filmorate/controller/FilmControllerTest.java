package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.utill.UtilReader;

import java.time.LocalDate;

@SpringBootTest
@AutoConfigureTestDatabase
public class FilmControllerTest {
    Film film;

    @Autowired
    private FilmController filmController;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void validateNotAddFilmWithEmptyName() {
        film = Film.builder()
                .name("")
                .description("Двое бандитов Винсент Вега и Джулс Винфилд ведут философские беседы в " +
                        "перерывах между разборками")
                .releaseDate(LocalDate.of(1994, 5, 21))
                .duration(154)
                .build();
        Assertions.assertThrows(ValidationException.class, () -> {
            filmController.addFilm(film);
        }, "Пустое название фильма");
    }

    @Test
    void validateNotAddFilmWithLongDescription() {
        film = Film.builder()
                .name("Криминальное чтиво")
                .description("Двое бандитов Винсент Вега и Джулс Винфилд ведут философские беседы в " +
                        "перерывах между разборками  и решением проблем с должниками криминального босса Марселласа " +
                        "Уоллеса.\n" +
                        "\n" +
                        "В первой истории Винсент проводит незабываемый вечер с женой Марселласа Мией. Во второй " +
                        "Марселлас покупает боксёра Бутча Кулиджа, чтобы тот сдал бой. В третьей истории" +
                        " Винсент и Джулс по нелепой случайности попадают в неприятности.")
                .releaseDate(LocalDate.of(1994, 5, 21))
                .duration(154)
                .build();
        Assertions.assertThrows(ValidationException.class, () -> {
            filmController.addFilm(film);
        }, "Описание превышает 200 символов");
    }

    @Test
    void validateNotAddFilmWithReleaseDateLaterThanNow() {
        film = Film.builder()
                .name("Криминальное чтиво")
                .description("Двое бандитов Винсент Вега и Джулс Винфилд ведут философские беседы в " +
                        "перерывах между разборками")
                .releaseDate(LocalDate.of(1800, 9, 21))
                .duration(154)
                .build();
        Assertions.assertThrows(ValidationException.class, () -> {
            filmController.addFilm(film);
        }, "Фильм создан ранее,чем 1895.12.28");
    }

    @Test
    void validateNotAddFilmIfDurationEqualsZero() {
        film = Film.builder()
                .name("Криминальное чтиво")
                .description("Двое бандитов Винсент Вега и Джулс Винфилд ведут философские беседы в " +
                        "перерывах между разборками")
                .releaseDate(LocalDate.of(1994, 5, 21))
                .duration(0)
                .build();
        Assertions.assertThrows(ValidationException.class, () -> {
            filmController.addFilm(film);
        }, "Продолжительность равно нулю");
    }

    @BeforeEach
    public void beforeEach() {
        jdbcTemplate.update(UtilReader.readString("src/test/resources/drop.sql"));
        jdbcTemplate.update(UtilReader.readString("src/main/resources/schema.sql"));
    }
}
