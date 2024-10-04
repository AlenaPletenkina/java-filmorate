package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.UserExistException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.utill.UtilReader;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Component(value = "H2UserDb")
@Repository
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private static final String SQL_QUERY_DIR = "src/main/resources/sql/query/user/";
    private static final String SELECT_ALL_SQL_QUERY = UtilReader.readString(SQL_QUERY_DIR + "select_all.sql");
    private static final String SELECT_BY_ID_SQL_QUERY = UtilReader.readString(SQL_QUERY_DIR + "select_by_id.sql");
    private static final String INSERT_SQL_QUERY = UtilReader.readString(SQL_QUERY_DIR + "insert.sql");
    private static final String UPDATE_SQL_QUERY = UtilReader.readString(SQL_QUERY_DIR + "update.sql");

    @Override
    public User createUser(User object) {
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement preparedStatement = connection
                        .prepareStatement(INSERT_SQL_QUERY,
                                Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1, object.getEmail());
                preparedStatement.setString(2, object.getLogin());
                preparedStatement.setString(3, object.getName());
                preparedStatement.setDate(4, Date.valueOf(object.getBirthday()));
                return preparedStatement;
            }, keyHolder);
            return getUserById(keyHolder.getKey().intValue());
        } catch (Exception e) {
            if (e.getMessage().contains("EMAIL")) {
                throw new UserExistException("EMAIL", object.getEmail());
            } else if (e.getMessage().contains("LOGIN")) {
                throw new UserExistException("LOGIN", object.getLogin());
            } else {
                throw new UnsupportedOperationException(e.getMessage());
            }
        }
    }

    @Override
    public User updateUser(User object) {
        jdbcTemplate.update(UPDATE_SQL_QUERY,
                object.getEmail(),
                object.getLogin(),
                object.getName(),
                Date.valueOf(object.getBirthday()),
                object.getId());
        return getUserById(object.getId());
    }

    @Override
    public List<User> getAllUsers() {
        return jdbcTemplate.query(SELECT_ALL_SQL_QUERY, new UserMapper());
    }

    @Override
    public User getUserById(Integer id) {
        return jdbcTemplate.query(SELECT_BY_ID_SQL_QUERY, new UserMapper(), id).stream().findAny().orElse(null);
    }

    public boolean contains(Integer userId) {
        return getUserById(userId) != null;
    }

    @Override
    public void deleteUser(User user) {
//TODO
    }

    @Override
    public void deleteUserById(Integer id) {
        String deleteSql = UtilReader.readString(SQL_QUERY_DIR + "delete_by_id.sql");
        jdbcTemplate.update(deleteSql, id);
    }
}