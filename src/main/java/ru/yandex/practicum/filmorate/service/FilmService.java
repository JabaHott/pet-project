package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.customExceptions.NotFoundException;
import ru.yandex.practicum.filmorate.customExceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.*;

@Slf4j
@Service
public class FilmService {
    private static final int MAX_SIZE_DESCRIPTION = 200;
    private static final LocalDate PAST_DATE = LocalDate.of(1895, 12, 28);
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
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
        filmStorage.addLike(filmId, userId);
        return filmId;
    }

    public Long removeLike(Long filmId, Long userId) {
        filmStorage.removeLike(filmId, userId);
        return filmId;
    }

    public List<Film> getMostPopular(int numberFilms) {
        return filmStorage.getAll().stream()
                .sorted((f1, f2) -> f2.getRate()- f1.getRate())
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
