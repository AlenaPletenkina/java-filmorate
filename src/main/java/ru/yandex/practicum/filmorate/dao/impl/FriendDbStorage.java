package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.friend.FriendStorage;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.utill.UtilReader;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;

@Repository
public class FriendDbStorage implements FriendStorage {

    private final JdbcTemplate jdbcTemplate;

    private static final String SQL_QUERY_DIR = "src/main/resources/sql/query/user/friend/";
    private static final String SELECT_ALL_SQL_QUERY = UtilReader.readString(SQL_QUERY_DIR + "select_all.sql");
    private static final String SELECT_COMMON_SQL_QUERY = UtilReader.readString(
            SQL_QUERY_DIR + "select_common.sql");
    private static final String SELECT_CONFIRMING_STATUS_SQL_QUERY = UtilReader.readString(
            SQL_QUERY_DIR + "select_confirming_status.sql");
    private static final String INSERT_SQL_QUERY = UtilReader.readString(SQL_QUERY_DIR + "insert.sql");
    private static final String DELETE_SQL_QUERY = UtilReader.readString(SQL_QUERY_DIR + "delete.sql");

    public FriendDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addFriend(Integer userIdOne, Integer userIdTwo) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection
                    .prepareStatement(INSERT_SQL_QUERY,
                            Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, userIdOne);
            preparedStatement.setInt(2, userIdTwo);
            preparedStatement.setBoolean(3, isFriend(userIdTwo, userIdOne));
            return preparedStatement;
        }, keyHolder);
    }

    @Override
    public void deleteFriend(Integer userIdOne, Integer userIdTwo) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection
                    .prepareStatement(DELETE_SQL_QUERY,
                            Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, userIdOne);
            preparedStatement.setInt(2, userIdTwo);
            return preparedStatement;
        }, keyHolder);
    }

    @Override
    public void deleteAllFriendsForUser(Integer userId) {
        String sql = UtilReader.readString(SQL_QUERY_DIR + "deleteAllFriends.sql");
        jdbcTemplate.update(sql, userId, userId);
    }

    @Override
    public Boolean isFriend(Integer userIdOne, Integer userIdTwo) {
        return jdbcTemplate.query(SELECT_CONFIRMING_STATUS_SQL_QUERY,
                        (rs, rowNum) -> rs.getObject("status", Boolean.class), userIdOne, userIdTwo, userIdTwo, userIdOne)
                .stream().anyMatch(Objects::nonNull);
    }

    @Override
    public List<User> getAllUserFriends(Integer userId) {
        return jdbcTemplate.query(SELECT_ALL_SQL_QUERY, new UserMapper(), userId);
    }

    @Override
    public List<User> getMutualFriends(Integer userIdOne, Integer userIdTwo) {
        return jdbcTemplate.query(SELECT_COMMON_SQL_QUERY, new UserMapper(), userIdOne, userIdTwo);
    }
}