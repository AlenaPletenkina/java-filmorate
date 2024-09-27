package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.utill.UtilReader;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Component(value = "H2FilmDb")
@Repository
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private static final String SQL_QUERY_DIR = "src/main/resources/sql/query/film/";
    private static final String SELECT_ALL_SQL_QUERY = UtilReader.readString(SQL_QUERY_DIR + "select_all.sql");
    private static final String SELECT_BY_ID_SQL_QUERY = UtilReader.readString(SQL_QUERY_DIR + "select_by_id.sql");
    private static final String INSERT_SQL_QUERY = UtilReader.readString(SQL_QUERY_DIR + "insert.sql");
    private static final String UPDATE_SQL_QUERY = UtilReader.readString(SQL_QUERY_DIR + "update.sql");
    private static final String SELECT_FAVORITE_FILMS_USER_ID_SQL_QUERY = UtilReader.readString(SQL_QUERY_DIR +
             "like/select_favorite_films_user_by_id.sql");
    private static final String SELECT_RECOMMENDATIONS_FILMS_ID_SQL_QUERY = UtilReader.readString(SQL_QUERY_DIR +
            "like/select_recommendations_films_id.sql");
    private static final String DIRECTOR_ORDER_BY_YEAR_SQL_QUERY = UtilReader.readString(SQL_QUERY_DIR +
            "director/director_order_by_year.sql");
    private static final String DIRECTOR_ORDER_BY_LIKES_SQL_QUERY = UtilReader.readString(SQL_QUERY_DIR +
            "director/director_order_by_likes.sql");
    private static final String SELECT_TOP_FILMS_WITH_FILTERS = UtilReader.readString(SQL_QUERY_DIR +
            "get_films_with_filters.sql");

    @Override
    public Film addFilm(Film object) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection
                    .prepareStatement(INSERT_SQL_QUERY,
                            Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, object.getName());
            preparedStatement.setString(2, object.getDescription());
            preparedStatement.setDate(3, Date.valueOf(object.getReleaseDate()));
            preparedStatement.setInt(4, object.getDuration());
            preparedStatement.setInt(5, object.getMpa().getId());
            return preparedStatement;
        }, keyHolder);
        return getFilm(keyHolder.getKey().intValue());
    }

    @Override
    public Film updateFilm(Film object) {
        jdbcTemplate.update(UPDATE_SQL_QUERY,
                object.getName(),
                object.getDescription(),
                Date.valueOf(object.getReleaseDate()),
                object.getDuration(),
                object.getMpa().getId(),
                object.getId());
        return getFilm(object.getId());
    }

    @Override
    public List<Film> getAllFilms() {
        return jdbcTemplate.query(SELECT_ALL_SQL_QUERY, new FilmMapper());
    }

    @Override
    public Film getFilm(Integer id) {
        return jdbcTemplate.query(SELECT_BY_ID_SQL_QUERY, new FilmMapper(), id).stream().findAny().orElse(null);
    }

    @Override
    public void deleteFilm(Film film) {
    }

    @Override
    public void deleteFilmById(Integer id) {
        String deleteSql = UtilReader.readString(SQL_QUERY_DIR + "delete_by_id.sql");
        jdbcTemplate.update(deleteSql, id);
    }

    public boolean contains(Integer filmId) {
        return getFilm(filmId) != null;
    }

    public List<Integer> getUsersRecommendations(Integer id) {
        return jdbcTemplate.queryForList(SELECT_RECOMMENDATIONS_FILMS_ID_SQL_QUERY, Integer.class, id, id);
    }

    public List<Integer> getFilmsUserById(Integer id) {
        return jdbcTemplate.queryForList(SELECT_FAVORITE_FILMS_USER_ID_SQL_QUERY, Integer.class, id);
    }

    @Override
    public List<Film> getTopFilmsWithFilters(Integer genreId, Integer year) {
        return jdbcTemplate.query(SELECT_TOP_FILMS_WITH_FILTERS,
                new Object[] { genreId, year},
                new FilmMapper());
    }

    @Override
    public List<Film> getSortedDirectorsFilmsByYears(long id) {

        List<Film> films = jdbcTemplate.query(DIRECTOR_ORDER_BY_YEAR_SQL_QUERY, new FilmMapper(), id);

        return films;
    }

    @Override
    public List<Film> getSortedDirectorsFilmsByLikes(long id) {

        List<Film> films = jdbcTemplate.query(DIRECTOR_ORDER_BY_LIKES_SQL_QUERY, new FilmMapper(), id);

        return films;
    }
}