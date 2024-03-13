package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.customExceptions.NotFoundException;
import ru.yandex.practicum.filmorate.customExceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Validated
@RestController
@Slf4j
public class FilmController {
    private Map<Integer, Film> films = new HashMap<>();
    private int id = 1;
    private static final int MAX_SIZE_DESCRIPTION = 200;
    private final LocalDate PAST_DATE = LocalDate.of(1895, 12, 28);

    @GetMapping("/films")
    public Collection<Film> findAll() {
        log.debug("Текущее количество фильмов: {}", films.size());
        return films.values();
    }

    @PostMapping(value = "/films")
    @Validated({FilmControllerInterface.create.class})
    public Film create(@Valid @RequestBody Film film) {
        validate(film);
        film.setId(id);
        log.debug("Сохранен фильм : {}", film.toString());
        films.put(id, film);
        id++;
        return film;
    }

    @PutMapping("/films")
    @Validated({FilmControllerInterface.update.class})
    public Film update(@Valid @RequestBody Film film) {
        if (!(films.containsKey(film.getId()))) {
            log.error("Фильм с id=" + film.getId() + "не найден!");
            throw new NotFoundException("Фильм с id=" + film.getId() + "не найден!");
        }
        validate(film);
        log.debug("Обновлены данные по фильму: {}", film.toString());
        films.put(film.getId(), film);
        return film;
    }

    private void validate(Film film) throws ValidationException, NotFoundException {
        if ((film.getName().isBlank() || film.getName().equals(null))) {
            log.warn("Получен фильм с пустым названием");
            throw new ValidationException("Название должно быть заполнено");
        }
        if (film.getDescription().length() > MAX_SIZE_DESCRIPTION) {
            log.warn("Размер описания превышен");
            throw new ValidationException("Описание не должно быть больше 200 символов");
        }
        if (film.getReleaseDate().isBefore(PAST_DATE)) {
            log.warn("Получен слишком старый фильм");
            throw new ValidationException("Дата выпуска должна быть после 28 декабря 1985 года.");
        }
        if (film.getDuration() < 0) {
            log.warn("Получен фильм с отрицательной длительностью");
            throw new ValidationException("Длительность фильма должна быть больше 0.");
        }
    }
}
