package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import ru.yandex.practicum.filmorate.controller.FilmControllerInterface;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    @Null(groups = FilmControllerInterface.Create.class)
    @NotNull(groups = FilmControllerInterface.Update.class)
    private Long id;
    @NotBlank
    private String name;
    private String description;
    @NotNull
    @JsonFormat
    private LocalDate releaseDate;
    @Positive
    private Integer duration;
    @PositiveOrZero
    private int rate;
    @JsonIgnore
    private Set<Long> likes;

    public Film(String name, String description, LocalDate releaseDate, Integer duration, int rate) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.rate = rate;
    }

    public Film() {
        this.likes = new HashSet<>();
    }

    public void addLike(Long id) {
        likes.add(id);
    }

    public void removeLike(Long id) {
        likes.remove(id);
    }
}
