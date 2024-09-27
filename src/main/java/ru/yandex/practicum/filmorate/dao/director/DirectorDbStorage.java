package ru.yandex.practicum.filmorate.dao.director;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.utill.UtilReader;

import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;
    private static final String SQL_QUERY_DIR = "src/main/resources/sql/query/director/";
    private static final String INSERT_SQL_QUERY = UtilReader.readString(SQL_QUERY_DIR + "insert.sql");
    private static final String SELECT_FILM_DIRECTORS_SQL_QUERY = UtilReader.readString(SQL_QUERY_DIR +
            "select_film_directors.sql");
    private static final String DELETE_FILM_DIRECTORS_SQL_QUERY = UtilReader.readString(SQL_QUERY_DIR +
            "delete_film_directors.sql");
    private static final String INSERT_DIRECTORS_SQL_QUERY = UtilReader.readString(SQL_QUERY_DIR +
            "insert_directors.sql");
    private static final String UPDATE_DIRECTORS_SQL_QUERY = UtilReader.readString(SQL_QUERY_DIR +
            "update_directors.sql");
    private static final String SELECT_ALL_DIRECTORS_SQL_QUERY = UtilReader.readString(SQL_QUERY_DIR +
            "select_all_directors.sql");
    private static final String SELECT_BY_ID_DIRECTORS_SQL_QUERY = UtilReader.readString(SQL_QUERY_DIR +
            "select_by_id_directors.sql");
    private static final String DELETE_BY_ID_DIRECTORS_SQL_QUERY = UtilReader.readString(SQL_QUERY_DIR +
            "delete_by_id_directors.sql");

    @Override
    public Director save(Director director) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(INSERT_DIRECTORS_SQL_QUERY, new String[]{"DIRECTOR_ID"});
            statement.setString(1, director.getName());
            return statement;
        }, keyHolder);
        director.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        return director;
    }

    @Override
    public Director update(Director director) {
        jdbcTemplate.update(UPDATE_DIRECTORS_SQL_QUERY, director.getName(), director.getId());
        return director;
    }

    @Override
    public Collection<Director> findAll() {
        return jdbcTemplate.query(SELECT_ALL_DIRECTORS_SQL_QUERY, new DirectorMapper());
    }

    @Override
    public Director findDirectorById(Integer id) {
        return jdbcTemplate.query(SELECT_BY_ID_DIRECTORS_SQL_QUERY, new DirectorMapper(), id)
                .stream().findAny().orElse(null);
    }

    @Override
    public boolean deleteDirector(Integer id) {
        return jdbcTemplate.update(DELETE_BY_ID_DIRECTORS_SQL_QUERY, id) > 0;
    }

    @Override
    public void setDirectors(Integer idFilm, Integer idDirector) {
        jdbcTemplate.update(INSERT_SQL_QUERY, idFilm, idDirector);
    }

    @Override
    public List<Director> getFilmDirectors(Integer filmId) {
        return jdbcTemplate.query(SELECT_FILM_DIRECTORS_SQL_QUERY, new DirectorMapper(), filmId);
    }

    @Override
    public void clearFilmDirectors(Integer filmId) {
        jdbcTemplate.update(DELETE_FILM_DIRECTORS_SQL_QUERY, filmId);
    }
}