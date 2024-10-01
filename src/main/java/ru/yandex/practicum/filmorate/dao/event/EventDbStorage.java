package ru.yandex.practicum.filmorate.dao.event;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.mapper.EventMapper;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@AllArgsConstructor
public class EventDbStorage implements EventStorage {

    private final NamedParameterJdbcOperations jdbcOperations;

    @Override
    public Optional<Event> getEventById(Integer eventId) {
        String sql = "SELECT * FROM EVENTS WHERE event_id = :eventId";
        List<Event> eventList = jdbcOperations.query(sql, Map.of("eventId", eventId), new EventMapper());
        if (!eventList.isEmpty()) {
            log.info("Найдено событие {}", eventList.get(0));
            return Optional.of(eventList.get(0));
        } else {
            log.info("Событие с ID {} не найдено", eventId);
            return Optional.empty();
        }
    }

    @Override
    public List<Event> getEventsByUserId(Integer userId) {
        String sql = "SELECT * FROM EVENTS WHERE user_id = :userId ";
        List<Event> events = jdbcOperations.query(sql, Map.of("userId", userId), new EventMapper());
        log.info("Найден список событий, состоящий из {} элементов", events.size());
        return events;
    }

    @Override
    public Event add(Event event) {
        log.info("Добавляем Event в список событий", event);
        String sqlQuery = "INSERT INTO EVENTS(user_id, event_type, entity_id, operation, timestamp) " +
                "VALUES (:userId, :eventType, :entityId, :operation, :timestamp) ";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("userId", event.getUserId());
        map.addValue("eventType", event.getEventType().toString());
        map.addValue("entityId", event.getEntityId());
        map.addValue("operation", event.getOperation().toString());
        map.addValue("timestamp", event.getTimestamp());
        jdbcOperations.update(sqlQuery, map, keyHolder);
        event.setEventId(keyHolder.getKey().intValue());
        log.info("Добавлено событие {}", event);
        return getEventById(event.getEventId()).get();
    }

    @Override
    public Event update(Event event) {
        String sql = "UPDATE EVENTS SET user_id = :userID, event_type = :eventType, entity_id = :entityId, " +
                "operation = :operation, timestamp = :timestamp WHERE event_id = :eventId";
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("eventId", event.getEventId());
        map.addValue("userId", event.getUserId());
        map.addValue("eventType", event.getEventType());
        map.addValue("entityId", event.getEntityId());
        map.addValue("operation", event.getOperation());
        map.addValue("timestamp", event.getTimestamp());
        jdbcOperations.update(sql, map);
        log.info("Изменено событие с ID {}", event.getEventId());
        return getEventById(event.getEventId()).get();
    }
}
