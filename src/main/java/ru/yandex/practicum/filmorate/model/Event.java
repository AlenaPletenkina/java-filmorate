package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "eventId")
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    private Integer eventId;
    @NotNull
    private Integer userId;
    @NotNull
    private EventType eventType;
    @NotNull
    private Integer entityId;
    @NotNull
    private Operation operation;
    private Long timestamp = Instant.now().toEpochMilli();

    public Event(Integer userId, EventType eventType, Integer entityId, Operation operation) {
        this.userId = userId;
        this.eventType = eventType;
        this.entityId = entityId;
        this.operation = operation;
    }
}
