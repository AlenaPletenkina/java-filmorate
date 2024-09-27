package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.utill.UtilReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Repository
@RequiredArgsConstructor

public class GenreDbStorage implements GenreDao {
    private final JdbcTemplate jdbcTemplate;
    private static final String SQL_QUERY_DIR = "src/main/resources/sql/query/genre/";
    private static final String SELECT_ALL_SQL_QUERY = UtilReader.readString(SQL_QUERY_DIR + "select_all.sql");
    private static final String SELECT_FILM_GENRES_SQL_QUERY = UtilReader.readString(SQL_QUERY_DIR + "select_film_genres.sql");
    private static final String SELECT_GENRES_ALL_FILMS_SQL_QUERY = UtilReader.readString(SQL_QUERY_DIR + "select_genres_all_films.sql");
    private static final String DELETE_FILM_GENRES_SQL_QUERY = UtilReader.readString(SQL_QUERY_DIR + "delete_film_genres.sql");
    private static final String SELECT_BY_ID_SQL_QUERY = UtilReader.readString(SQL_QUERY_DIR + "select_by_id.sql");
    private static final String INSERT_SQL_QUERY = UtilReader.readString(SQL_QUERY_DIR + "insert.sql");

    @Override
    public List<Genre> getFilmGenres(Integer filmId) {
        return jdbcTemplate.query(SELECT_FILM_GENRES_SQL_QUERY, new GenreMapper(), filmId);
    }

    @Override
    public List<Map<Integer, Genre>> getAllFilmsGenres() {
        return jdbcTemplate.query(SELECT_GENRES_ALL_FILMS_SQL_QUERY,
                (rs, rowNum) -> {
                    Map<Integer, Genre> result = new HashMap<>();
                    result.put(rs.getInt("film_id"),
                            new Genre(rs.getInt("genre_id"),
                                    rs.getString("genre_name")));
                    return result;
                });
    }

    @Override
    public void setGenres(Integer filmId, Integer genreId) {
        jdbcTemplate.update(INSERT_SQL_QUERY, filmId, genreId);
    }

    @Override
    public List<Genre> getGenres() {
        return new ArrayList<>(jdbcTemplate.query(SELECT_ALL_SQL_QUERY, new GenreMapper()));
    }

    @Override
    public Genre getGenre(Integer genreId) {
        return jdbcTemplate.query(SELECT_BY_ID_SQL_QUERY, new GenreMapper(), genreId)
                .stream().findAny().orElse(null);
    }

    @Override
    public void clearFilmGenres(Integer filmId) {
        jdbcTemplate.update(DELETE_FILM_GENRES_SQL_QUERY, filmId);
    }

    @Override
    public boolean contains(Integer genreId) {
        return getGenre(genreId) != null;
    }
}