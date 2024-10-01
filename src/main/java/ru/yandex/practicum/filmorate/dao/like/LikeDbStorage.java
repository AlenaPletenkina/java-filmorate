package ru.yandex.practicum.filmorate.dao.like;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.utill.UtilReader;

import java.sql.PreparedStatement;
import java.sql.Statement;

@Repository
@Slf4j
public class LikeDbStorage implements LikeStorage {

    private final JdbcTemplate jdbcTemplate;
    private static final String SQL_QUERY_DIR = "src/main/resources/sql/query/film/like/";
    private static final String INSERT_SQL_QUERY = UtilReader.readString(SQL_QUERY_DIR + "insert.sql");
    private static final String DELETE_SQL_QUERY = UtilReader.readString(SQL_QUERY_DIR + "delete.sql");

    public LikeDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public void addLike(Integer filmId, Integer userId) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection
                    .prepareStatement(INSERT_SQL_QUERY,
                            Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, filmId);
            preparedStatement.setInt(2, userId);
            return preparedStatement;
        }, keyHolder);
    }

    @Override
    public void deleteLike(Integer filmId, Integer userId) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection
                    .prepareStatement(DELETE_SQL_QUERY,
                            Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, filmId);
            preparedStatement.setInt(2, userId);
            return preparedStatement;
        }, keyHolder);
    }

    @Override
    public void deleteLikesByFilmId(Integer filmId) {
        String sql = UtilReader.readString(SQL_QUERY_DIR + "deleteByFilmId.sql");
        jdbcTemplate.update(sql, filmId);
    }

}