package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.customExceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component("inMemoryFilmStorage")
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> filmStorage = new HashMap<>();
    private Long filmIdCounter = 0L;
    private final UserStorage userStorage;

    @Autowired
    public InMemoryFilmStorage(@Qualifier("inMemoryUserStorage") UserStorage inMemoryUserStorage) {
        this.userStorage = inMemoryUserStorage;
    }


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

    @Override
    public Long addLike(Long filmId, Long userId) {
        User user = userStorage.get(userId); // оставил для проверки есть пользователь или нет
        Film film = get(filmId);
        log.debug("Пользователь {} поставил лайк фильму {}", userId, film.getId());
        film.addLike(userId);
        if (film.getLikes().contains(userId)) {
            film.setRate(film.getRate() + 1);
        }
        update(film);
        return film.getId();
    }

    @Override
    public Long removeLike(Long filmId, Long userId) {
        User user = userStorage.get(userId); // оставил для проверки есть пользователь или нет
        Film film = get(filmId);
        log.debug("Пользователь {} снял лайк с фильма {}", userId, film.getId());
        film.removeLike(filmId);
        if (film.getLikes().contains(userId)) {
            film.setRate(film.getRate() - 1);
        }
        return film.getId();
    }
}
