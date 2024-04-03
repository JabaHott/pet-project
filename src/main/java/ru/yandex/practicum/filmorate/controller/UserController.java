package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.customExceptions.NotFoundException;
import ru.yandex.practicum.filmorate.customExceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

@RestController
@Slf4j
public class UserController implements UserControllerInterface {
    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public Collection<User> findAll() {
        return userService.getAll().values();
    }

    @PostMapping(value = "/users")
    @Validated({Create.class})
    public User create(@Valid @RequestBody User user) {
        userService.create(user);
        return user;
    }

    @PutMapping("/users")
    @Validated({Update.class})
    public User update(@Valid @RequestBody User user) {
        userService.update(user);
        return user;
    }

    @GetMapping({"users/{id}"})
    public User getUser(@PathVariable(name = "id") Long userId) {
        return userService.get(userId);
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public void addFriend(@PathVariable(name = "id") Long userId, @PathVariable(name = "friendId") Long friendId) {
        userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable(name = "id") Long userId, @PathVariable(name = "friendId") Long friendId) {
        userService.removeFriend(userId, friendId);
    }

    @GetMapping("/users/{id}/friends")
    public List<User> getFriends(@PathVariable(name = "id") Long id) {
        return userService.getFriends(id);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable(name = "id") Long userId, @PathVariable(name = "otherId") Long otherUserId) {
        return userService.getCommonFriends(userId, otherUserId);
    }
}
