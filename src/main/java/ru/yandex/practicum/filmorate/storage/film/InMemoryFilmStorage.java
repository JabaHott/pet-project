package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.customExceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    Map<Long, Film> filmMap = new HashMap<>();
    private Long filmIdCounter = 1L;


    @Override
    public Film create(Film film) {
        film.setId(filmIdCounter);
        filmMap.put(filmIdCounter, film);
        filmIdCounter++;
        return film;
    }

    @Override
    public Film update(Film film) {
        if (!filmMap.containsKey(film.getId())) {
            throw new NotFoundException("Фильм с id=" + film.getId() + "не найден!");
        }
        filmMap.put(film.getId(), film);
        return film;
    }

    @Override
    public Film get(Long id) {
        if (!filmMap.containsKey(id)) {
            throw new NotFoundException("Фильм с id=" + id + "не найден!");
        }
        return filmMap.get(id);
    }

    @Override
    public Map<Long, Film> getAll() {
        return filmMap;
    }
}
