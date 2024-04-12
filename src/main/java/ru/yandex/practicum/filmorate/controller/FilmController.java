package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.customExceptions.NotFoundException;
import ru.yandex.practicum.filmorate.customExceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@Validated
@RestController
@Slf4j
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/films")
    public Collection<Film> findAll() {
        log.info("Получен GET /films запрос");
        Collection<Film> response = filmService.getAll();
        log.info("ОТправлен ответ GET /user с телом {}", response);
        return response;
    }

    @PostMapping(value = "/films")
    @Validated({FilmControllerInterface.Create.class})
    public Film create(@Valid @RequestBody Film film) throws ValidationException {
        log.info("Получен Post запрос /films");
        Film response = filmService.create(film);
        log.info("Отправлен ответ Post /films с телом {}", response);
        return response;
    }

    @PutMapping("/films")
    @Validated({FilmControllerInterface.Update.class})
    public Film update(@Valid @RequestBody Film film) throws NotFoundException, ValidationException {
        log.info("Получен PUT запрос /films");
        Film response = filmService.update(film);
        log.info("Отправлен PUT запрос /films с телом {}", response);
        return response;
    }

    @GetMapping("films/{id}")
    public Film get(@PathVariable(name = "id") Long id) throws NotFoundException {
        log.info("Получен GET запрос films/id");
        Film response = filmService.get(id);
        log.info("Отправлен ответ GET films/id с телом {}", response);
        return response;
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void like(@PathVariable(name = "id") Long filmId, @PathVariable(name = "userId") Long userId) throws NotFoundException {
        log.info("Получен PUT /films/id/like/userId");
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void removeLike(@PathVariable(name = "id") Long filmId, @PathVariable(name = "userId") Long userId) throws NotFoundException {
        log.info("Получен DELETE /films/id/like/userId");
        filmService.removeLike(filmId, userId);
    }

    @GetMapping("/films/popular")
    public List<Film> findMostPopular(@RequestParam(name = "count", defaultValue = "10") int numberFilms) {
        log.info("Получен GET /films/popular");
        List<Film> response = filmService.getMostPopular(numberFilms);
        log.info("ОТправлен ответ GET /films/popular с телом {}", response);
        return response;
    }

}
