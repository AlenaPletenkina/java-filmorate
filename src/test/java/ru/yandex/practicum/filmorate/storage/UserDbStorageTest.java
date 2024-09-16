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
import ru.yandex.practicum.filmorate.dao.impl.UserDbStorage;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.utill.UtilReader;

import java.time.LocalDate;

@JdbcTest
@AutoConfigureTestDatabase
@Import(UserDbStorage.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)

public class UserDbStorageTest {
    private final UserDbStorage userStorage;
    private final JdbcTemplate jdbcTemplate;

    User testUser = new User(
            null,
            "filmorate@yandex.ru",
            "yandex",
            "filmorate",
            LocalDate.of(1993, 6, 4));

    @BeforeEach
    void beforeEach() {
        jdbcTemplate.update(UtilReader.readString("src/test/resources/drop.sql"));
        jdbcTemplate.update(UtilReader.readString("src/main/resources/schema.sql"));
        jdbcTemplate.update(UtilReader.readString("src/test/resources/data.sql"));
    }

    @Test
    void getAllTest() {
        int[] idArray = userStorage.getAllUsers()
                .stream().mapToInt(User::getId).toArray();
        Assertions.assertArrayEquals(new int[]{1, 2, 3}, idArray);
    }

    @Test
    void getById() {
        User user = new User(1,
                "trauma@email.xyz",
                "trauma",
                "Robert",
                LocalDate.of(1995, 10, 2));
        Assertions.assertEquals(user, userStorage.getUserById(1));
    }

    @Test
    void addTest() {
        testUser.setId(4);
        Assertions.assertEquals(testUser, userStorage.createUser(testUser));
    }

    @Test
    void updateTest() {
        testUser.setId(1);
        Assertions.assertEquals(testUser, userStorage.updateUser(testUser));
    }
}
