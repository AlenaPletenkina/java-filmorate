package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Review {

    Integer reviewId;
    String content;
    Boolean isPositive;
    Integer userId;
    Integer filmId;
    int useful;
}