package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Like {
    private final Long userId;
    private final Long filmId;
}
