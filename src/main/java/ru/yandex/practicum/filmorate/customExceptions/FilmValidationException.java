package ru.yandex.practicum.filmorate.customExceptions;

public class FilmValidationException extends RuntimeException {
    public FilmValidationException(String message) {
        super(message);
    }
}
