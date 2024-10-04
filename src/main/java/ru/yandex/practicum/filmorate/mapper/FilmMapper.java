package ru.yandex.practicum.filmorate.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Set;

@Service
public class FilmMapper implements RowMapper<Film> {

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Rating mpa = new Rating(rs.getInt("rating_id"), rs.getString("rating_name"));
        Set<Genre> genres = new LinkedHashSet<>();
        Set<Director> directors = new LinkedHashSet<>();

        Film film = Film.builder()
                .name(rs.getString("film_name"))
                .id(rs.getInt("film_id"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(mpa)
                .likes(rs.getInt("rate"))
                .genres(genres)
                .directors(directors)
                .build();

        return film;
    }
}