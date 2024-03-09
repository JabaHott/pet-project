package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.customExceptions.FilmValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class FilmController {
    private Map<Integer, Film> films = new HashMap<>();
    private int id = 1;
    LocalDate localDate = LocalDate.of(1985, 12, 28);

    @GetMapping("/films")
    public Collection<Film> findAll() {
        log.debug("Текущее количество фильмов: {}", films.size());
        return films.values();
    }

    @PostMapping(value = "/films")
    public Film create(@Valid @RequestBody Film film) {
        validate(film);
        film.setId(id);
        log.debug("Сохранен фильм : {}", film.toString());
        films.put(id, film);
        id++;
        return film;
    }

    @PutMapping("/films")
    public Film update(@Valid @RequestBody Film film) {
        validate(film);
        if (films.containsKey(film.getId())) {
            log.debug("Обновлены данные по фильму: {}", film.toString());
            films.remove(film.getId());
            films.put(film.getId(), film);
            return film;
        } else {
            log.warn("Фильм с id=" + film.getId() + "не найден!");
            throw new FilmValidationException("Фильм не найден!");
        }
    }

    private void validate(Film film) {
        if (!(film.getName().isEmpty())) {
            if (!(film.getDescription().length() > 200)) {
                if (!(film.getReleaseDate().isBefore(localDate))) {
                    if (!(film.getDuration() < 0)) {
                        return;
                    } else {
                        log.warn("Получен фильм с отрицательной длительностью");
                        throw new FilmValidationException("Длительность фильма должна быть больше 0.");
                    }
                } else {
                    log.warn("Получен слишком старый фильм");
                    throw new FilmValidationException("Дата выпуска должна быть после 28 декабря 1985 года.");
                }
            } else {
                log.warn("Размер описания превышен");
                throw new FilmValidationException("Описание не должно быть больше 200 символов");
            }
        } else {
            log.warn("Получен фильм с пустым названием");
            throw new FilmValidationException("Название должно быть заполнено");
        }
    }
}
