package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dao.impl.RatingDbStorage;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import(RatingDbStorage.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)

public class RatingDbStorageTest {
    private final RatingDbStorage ratingDbStorage;

    @Test
    public void shouldGetAllRatingMpa() {
        List<Rating> listMPA = ratingDbStorage.getRatingList().stream().toList();

        assertThat(listMPA.size())
                .isEqualTo(5);
    }
}

