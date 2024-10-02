package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "eventId")
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Event {

    Integer eventId;
    @NotNull
    Integer userId;
    @NotNull
    EventType eventType;
    @NotNull
    Integer entityId;
    @NotNull
    Operation operation;
    Long timestamp = Instant.now().toEpochMilli();

    public Event(Integer userId, EventType eventType, Integer entityId, Operation operation) {
        this.userId = userId;
        this.eventType = eventType;
        this.entityId = entityId;
        this.operation = operation;
    }
}
