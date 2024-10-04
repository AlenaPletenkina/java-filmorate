package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.RatingDao;
import ru.yandex.practicum.filmorate.mapper.RatingMapper;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.utill.UtilReader;

import java.util.List;

@Component
@Repository
@RequiredArgsConstructor

public class RatingDbStorage implements RatingDao {
    private final JdbcTemplate jdbcTemplate;
    private static final String SQL_QUERY_DIR = "src/main/resources/sql/query/mpa_rating/";
    private static final String SELECT_ALL_SQL_QUERY = UtilReader.readString(SQL_QUERY_DIR + "select_all.sql");
    private static final String SELECT_BY_ID_SQL_QUERY = UtilReader.readString(SQL_QUERY_DIR + "select_by_id.sql");

    @Override
    public List<Rating> getRatingList() {
        return jdbcTemplate.query(SELECT_ALL_SQL_QUERY, new RatingMapper());
    }

    @Override
    public Rating getRating(Integer ratingId) {
        return jdbcTemplate.query(SELECT_BY_ID_SQL_QUERY, new RatingMapper(), ratingId)
                .stream().findAny().orElse(null);
    }

    @Override
    public boolean contains(Integer ratingId) {
        return getRating(ratingId) != null;
    }
}