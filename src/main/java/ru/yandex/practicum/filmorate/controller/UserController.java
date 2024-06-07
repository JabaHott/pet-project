package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.*;

@RestController
@Slf4j
@RequiredArgsConstructor
public class UserController implements UserControllerInterface {
    private final UserService userService;

    @GetMapping("/users")
    public Collection<User> findAll(Model model) {
        log.info("Получен GET /user запрос");
        Collection<User> response = userService.getAll();
        model.addAttribute("users", response);
        log.info("ОТправлен ответ GET /user с телом {}", response);
        return response;
    }

    @PostMapping(value = "/users")
    @Validated({Create.class})
    public User create(@Valid @RequestBody User user) {
        log.info("Пришел Post запрос /users с телом {}", user);
        User response = userService.create(user);
        log.info("Отправлен ответ Post /users с телом {}", response);
        return response;
    }

    @PutMapping("/users")
    @Validated({Update.class})
    public User update(@Valid @RequestBody User user) {
        log.info("Пришел Put запрос /users с телом {}", user);
        User response = userService.update(user);
        log.info("Отправлен ответ Put /users с телом {}", response);
        return response;
    }

    @GetMapping({"users/{id}"})
    public User getUser(@PathVariable(name = "id") Long userId) {
        log.info("Пришел GET запрос /users/id с телом {}", userId);
        User response = userService.get(userId);
        log.info("Отправлен ответ Put /users/id с телом {}", response);
        return response;
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public void addFriend(@PathVariable(name = "id") Long userId, @PathVariable(name = "friendId") Long friendId) {
        log.info("Пришел GET запрос /users/id/friends/id с телом {}", userId);
        userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable(name = "id") Long userId, @PathVariable(name = "friendId") Long friendId) {
        log.info("Пришел DELETE запрос /users/id/friends/friendId с параметрами {}, {}", userId, friendId);
        userService.removeFriend(userId, friendId);
    }

    @GetMapping("/users/{id}/friends")
    public List<User> getFriends(@PathVariable(name = "id") Long id) {
        log.info("Получен GET запрос /users/id/friends с параметром {}", id);
        return userService.getFriends(id);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable(name = "id") Long userId, @PathVariable(name = "otherId") Long otherUserId) {
        log.info("Получен GET запрос /users/id/friends/common/otherId с параметром {}, {}", userId, otherUserId);
        return userService.getCommonFriends(userId, otherUserId);
    }
}
