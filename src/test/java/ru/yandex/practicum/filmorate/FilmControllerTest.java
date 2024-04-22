package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.customExceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.*;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.Assert.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
public class FilmControllerTest {

    private final FilmService filmService;
    private final UserService userService;
    private static Validator validator;
    private static final String FILM_NAME = "SHREK";
    private static final String FILM_DESCRIPTION = "SHREK IS THE BEST FILM";
    private static final Integer FILM_DURATION = 90;
    private static final LocalDate RELEASE_DATE = LocalDate.of(2001, 10, 31);
    private static final Integer RATE = 10;
    private static final Mpa mpa = new Mpa(2l, "PG");

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

    @Test
    public void createFilm() {
        Film film = Film.builder()
                .name(FILM_NAME)
                .description(FILM_DESCRIPTION)
                .releaseDate(RELEASE_DATE)
                .duration(FILM_DURATION)
                .rate(RATE)
                .mpa(mpa)
                .build();
        filmService.create(film);
        assertTrue(filmService.getAll().contains(film));
    }

    @Test
    public void blankName() {
        Film film = filmService.create(Film.builder()
                .name("")
                .description(FILM_DESCRIPTION)
                .releaseDate(RELEASE_DATE)
                .duration(FILM_DURATION)
                .rate(RATE)
                .mpa(mpa)
                .build());
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
    }

    @Test
    public void releaseDateNotValid() {
        Film film = Film.builder()
                .name(FILM_NAME)
                .description(FILM_DESCRIPTION)
                .releaseDate(LocalDate.of(1700, 01, 01))
                .duration(FILM_DURATION)
                .rate(RATE)
                .mpa(mpa)
                .build();
        assertThrows(ValidationException.class, () -> {
            filmService.create(film);
        });
    }

    @Test
    public void notValidDescript() {
        Film film = Film.builder()
                .name(FILM_NAME)
                .description("a".repeat(201))
                .releaseDate(RELEASE_DATE)
                .duration(FILM_DURATION)
                .rate(RATE)
                .mpa(mpa)
                .build();
        assertThrows(ValidationException.class, () -> {
            filmService.create(film);
        });
    }

    @Test
    void findById() {
        Film film = Film.builder()
                .name(FILM_NAME)
                .description(FILM_DESCRIPTION)
                .releaseDate(RELEASE_DATE)
                .duration(FILM_DURATION)
                .rate(RATE)
                .mpa(mpa)
                .build();
        filmService.create(film);
        assertEquals(film, filmService.get(film.getId()));
    }

    @Test
    public void shouldUpdate() {
        Film film = Film.builder()
                .name(FILM_NAME)
                .description(FILM_DESCRIPTION)
                .releaseDate(RELEASE_DATE)
                .duration(FILM_DURATION)
                .rate(RATE)
                .mpa(mpa)
                .build();
        filmService.create(film);
        Film film1 = Film.builder()
                .id(film.getId())
                .name(FILM_NAME)
                .description(FILM_DESCRIPTION)
                .releaseDate(RELEASE_DATE)
                .duration(FILM_DURATION + 1)
                .rate(RATE)
                .mpa(mpa)
                .build();
        filmService.update(film1);
        assertEquals(film1, filmService.get(film.getId()));
    }

    @Test
    public void addLike() {
        Film film = Film.builder()
                .name(FILM_NAME)
                .description(FILM_DESCRIPTION)
                .releaseDate(RELEASE_DATE)
                .duration(FILM_DURATION)
                .rate(RATE)
                .mpa(mpa)
                .build();
        filmService.create(film);
        User user = User.builder()
                .login("SmokeMaster")
                .name("Daite_pokurit")
                .email("qwertyuiopasd@yandex.ru")
                .birthday(LocalDate.of(2001, 4, 14))
                .build();
        userService.create(user);
        filmService.addLike(film.getId(), user.getId());
        assertEquals(1, filmService.get(film.getId()).getLikes().size());
    }

    @Test
    public void removeLike() {
        Film film = Film.builder()
                .name(FILM_NAME)
                .description(FILM_DESCRIPTION)
                .releaseDate(RELEASE_DATE)
                .duration(FILM_DURATION)
                .rate(RATE)
                .mpa(mpa)
                .build();
        filmService.create(film);
        User user = User.builder()
                .login("SmokeMaster")
                .name("Daite_pokurit")
                .email("qwertyuiopasd@yandex.ru")
                .birthday(LocalDate.of(2001, 4, 14))
                .build();
        userService.create(user);
        filmService.addLike(film.getId(), user.getId());
        filmService.removeLike(film.getId(), user.getId());
        assertEquals(0, filmService.get(film.getId()).getLikes().size());
    }

    @Test
    public void getPopular() {
        Film film = Film.builder()
                .name(FILM_NAME)
                .description(FILM_DESCRIPTION)
                .releaseDate(RELEASE_DATE)
                .duration(FILM_DURATION)
                .rate(RATE)
                .mpa(mpa)
                .build();
        Film film1 = Film.builder()
                .name(FILM_NAME.repeat(2))
                .description(FILM_DESCRIPTION)
                .releaseDate(RELEASE_DATE)
                .duration(FILM_DURATION)
                .rate(RATE)
                .mpa(mpa)
                .build();
        filmService.create(film);
        filmService.create(film1);
        User user = User.builder()
                .login("SmokeMaster")
                .name("Daite_pokurit")
                .email("qwertyuiopasd@yandex.ru")
                .birthday(LocalDate.of(2001, 4, 14))
                .build();
        userService.create(user);
        User user1 = User.builder()
                .login("SmokeMaster1")
                .name("Daite_pokurit1")
                .email("qwertyuiopasd1@yandex.ru")
                .birthday(LocalDate.of(2001, 4, 14))
                .build();
        userService.create(user1);
        filmService.addLike(film.getId(), user.getId());
        filmService.addLike(film.getId(), user1.getId());
        filmService.addLike(film1.getId(), user.getId());
        film = filmService.get(film.getId());
        film1 = filmService.get(film1.getId());
        for (Film film2 : filmService.getMostPopular(1)) {
            assertEquals(film, film2);
        }
    }
}
