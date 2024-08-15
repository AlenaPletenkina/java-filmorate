package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
public class Film {
    Integer id;
    String name;
    String description;
    LocalDate releaseDate;
    Integer duration;
    @Singular
    Set<Integer> likes;

}
