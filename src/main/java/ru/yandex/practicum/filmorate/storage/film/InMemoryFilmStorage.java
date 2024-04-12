package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.customExceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> filmStorage = new HashMap<>();
    private Long filmIdCounter = 0L;


    @Override
    public Film create(Film film) {
        Long id = ++filmIdCounter;
        film.setId(id);
        filmStorage.put(id, film);
        return film;
    }

    @Override
    public Film update(Film film) {
        if (!filmStorage.containsKey(film.getId())) {
            throw new NotFoundException("Фильм с id=" + film.getId() + "не найден!");
        }
        filmStorage.put(film.getId(), film);
        return film;
    }

    @Override
    public Film get(Long id) {
        if (!filmStorage.containsKey(id)) {
            throw new NotFoundException("Фильм с id=" + id + "не найден!");
        }
        return filmStorage.get(id);
    }

    @Override
    public Collection<Film> getAll() {
        return filmStorage.values();
    }
}
