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
    private FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/films")
    public Collection<Film> findAll() {
        return filmService.getAll().values();
    }

    @PostMapping(value = "/films")
    @Validated({FilmControllerInterface.Create.class})
    public Film create(@Valid @RequestBody Film film) throws ValidationException {
        return filmService.create(film);
    }

    @PutMapping("/films")
    @Validated({FilmControllerInterface.Update.class})
    public Film update(@Valid @RequestBody Film film) throws NotFoundException, ValidationException {
        return filmService.update(film);
    }

    @GetMapping("films/{id}")
    public Film get(@PathVariable(name = "id") Long id) throws NotFoundException {
        return filmService.get(id);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void like(@PathVariable(name = "id") Long filmId, @PathVariable(name = "userId") Long userId) throws NotFoundException {
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void removeLike(@PathVariable(name = "id") Long filmId, @PathVariable(name = "userId") Long userId) throws NotFoundException {
        filmService.removeLike(filmId, userId);
    }

    @GetMapping("/films/popular")
    public List<Film> findMostPopular(@RequestParam(name = "count", defaultValue = "10") int numberFilms) {
        return filmService.getMostPopular(numberFilms);
    }

}
