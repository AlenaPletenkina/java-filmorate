package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
public class User {
    Integer id;
    String email;
    String login;
    String name;
    LocalDate birthday;
    @Singular
    Set<Integer> friends;
}
