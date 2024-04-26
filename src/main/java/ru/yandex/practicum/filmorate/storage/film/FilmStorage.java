package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Film create(Film film);

    Film update(Film film);

    Film get(Long id);

    Collection<Film> getAll();

    Long addLike(Long filmId, Long userId);

    Long removeLike(Long filmId, Long userId);
}
