package ru.yandex.practicum.filmorate.dao.event;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;
import java.util.Optional;

public interface EventStorage {

    Optional<Event> getEventById(Integer eventId);

    List<Event> getEventsByUserId(Integer userId);

    Event add(Event event);

    Event update(Event event);
}