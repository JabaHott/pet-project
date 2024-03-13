package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.yandex.practicum.filmorate.controller.FilmControllerInterface;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Positive;
import java.time.LocalDate;

/**
 * Film.
 */
@Data
public class Film {
    @Null(groups = FilmControllerInterface.Create.class)
    @NotNull(groups = FilmControllerInterface.Update.class)
    private Integer id;
    @NotBlank
    private String name;
    private String description;
    @NotNull
    @JsonFormat
    private LocalDate releaseDate;
    @Positive
    private Integer duration;

    public Film(String name, String description, LocalDate releaseDate, Integer duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }
}
