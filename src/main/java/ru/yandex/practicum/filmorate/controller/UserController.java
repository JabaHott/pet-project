package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.customExceptions.NotFoundException;
import ru.yandex.practicum.filmorate.customExceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class UserController implements UserControllerInterface {
    private Map<Integer, User> users = new HashMap<>();

    private Integer id = 1;

    @GetMapping("/users")
    public Collection<User> findAll() {
        log.debug("Текущее количество пользователей: {}", users.size());
        return users.values();
    }

    @PostMapping(value = "/users")
    @Validated({Create.class})
    public User create(@Valid @RequestBody User user) {
        validate(user);
        user.setId(id);
        log.debug("Сохранен пользователь : {}", user.toString());
        users.put(id, user);
        id++;
        return user;
    }

    @PutMapping("/users")
    @Validated({Update.class})
    public User update(@Valid @RequestBody User user) {
        if (!(users.containsKey(user.getId()))) {
            log.warn("Пользователь с id=" + user.getId() + " не найден");
            throw new NotFoundException("Пользователь с id=" + user.getId() + " не найден");
        }
        validate(user);
        log.debug("Изменены данные по пользователю: {}", user.toString());
        users.put(user.getId(), user);
        return user;
    }

    private void validate(User user) {
        if (user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
            log.warn("Получена некорректная почта");
            throw new ValidationException("Почта должна быть заполнена и содержать @.");
        }
        if (user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            log.warn("Получен некорректный логин");
            throw new ValidationException("Логин не должен быть пустой и не должен содержать пробелы");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Получен пользователь с некорректной датой рождения");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
