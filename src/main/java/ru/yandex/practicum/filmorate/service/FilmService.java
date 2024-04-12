package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.customExceptions.NotFoundException;
import ru.yandex.practicum.filmorate.customExceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.*;

@Slf4j
@Service
public class FilmService {
    private static final int MAX_SIZE_DESCRIPTION = 200;
    private static final LocalDate PAST_DATE = LocalDate.of(1895, 12, 28);
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film create(Film film) {
        validate(film);
        filmStorage.create(film);
        log.debug("Сохранен пользователь : {}", film);
        return film;
    }

    public Film update(Film film) {
        validate(film);
        filmStorage.update(film);
        log.debug("Изменены данные по пользователю: {}", film);
        return film;
    }

    public Film get(Long id) {
        log.debug("Запрошены данные по id = {}", id);
        return filmStorage.get(id);
    }

    public Collection<Film> getAll() {
        log.debug("Направлен запрос по всем пользователям");
        return filmStorage.getAll();
    }

    public Long addLike(Long filmId, Long userId) {
        User user = userStorage.get(userId); // оставил для проверки есть пользователь или нет
        Film film = get(filmId);
        log.debug("Пользователь {} поставил лайк фильму {}", userId, film.getId());
        film.addLike(userId);
        if (film.getLikes().contains(userId)) {
            film.setRate(film.getRate() + 1);
        }
        filmStorage.update(film);
        return film.getId();
    }

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

    public List<Film> getMostPopular(int numberFilms) {
        return getAll().stream()
                .sorted(Comparator.comparingLong(Film::getId))
                .sorted(Comparator.comparingInt(Film::getRate).reversed())
                .limit(numberFilms)
                .collect(Collectors.toList());
    }

    private void validate(Film film) throws ValidationException, NotFoundException {
        if (film.getDescription().length() > MAX_SIZE_DESCRIPTION) {
            log.warn("Размер описания превышен");
            throw new ValidationException("Описание не должно быть больше 200 символов");
        }
        if (film.getReleaseDate().isBefore(PAST_DATE)) {
            log.warn("Получен слишком старый фильм");
            throw new ValidationException("Дата выпуска должна быть после 28 декабря 1985 года.");
        }
    }
}
