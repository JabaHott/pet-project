package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.customExceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.customExceptions.UserValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class UserController {
    private Map<Integer, User> users = new HashMap<>();

    private Integer id = 1;

    @GetMapping("/users")
    public Collection<User> findAll() {
        log.debug("Текущее количество пользователей: {}", users.size());
        return users.values();
    }

    @PostMapping(value = "/users")
    public User create(@Valid @RequestBody User user) {
        validate(user);
        user.setId(id);
        log.debug("Сохранен пользователь : {}", user.toString());
        users.put(id, user);
        id++;
        return user;
    }

    @PutMapping("/users")
    public User update(@Valid @RequestBody User user) {
        if (users.containsKey(user.getId())) {
            log.debug("Изменены данные по пользователю: {}", user.toString());
            users.put(user.getId(), user);
            users.remove(user.getId());
            return user;
        } else {
            log.warn("Пользователь с id=" + user.getId() + " не найден");
            throw new UserNotFoundException("Пользователь с id=" + user.getId() + " не найден");
        }
    }

    private void validate(User user) {
        if (!(user.getEmail().isEmpty() || !user.getEmail().contains("@"))) {
            if (!(user.getLogin().isEmpty() || user.getLogin().contains(" "))) {
                if (!(user.getBirthday().isAfter(LocalDate.now()))) {
                    if (user.getName() == null || user.getName().isBlank()) {
                        user.setName(user.getLogin());
                    }
                } else {
                    throw new UserValidationException("Дата рождения не может быть в будущем");
                }
            } else {
                throw new UserValidationException("Логин не должен быть пустой и не должен содержать пробелы");
            }
        } else {
            throw new UserValidationException("Почта должна быть заполнена и содержать @.");
        }
    }
}
