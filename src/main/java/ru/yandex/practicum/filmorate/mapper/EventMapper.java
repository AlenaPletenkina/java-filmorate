package ru.yandex.practicum.filmorate.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EventMapper implements RowMapper<Event> {

    @Override
    public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Event(rs.getInt("event_id"),
                rs.getInt("user_id"),
                EventType.valueOf(rs.getString("event_type")),
                rs.getInt("entity_id"),
                Operation.valueOf(rs.getString("operation")),
                rs.getLong("timestamp"));
    }
}
