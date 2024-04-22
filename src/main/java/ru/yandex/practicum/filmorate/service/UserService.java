package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.customExceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User create(User user) {
        validate(user);
        userStorage.create(user);
        log.debug("Сохранен пользователь : {}", user);
        return user;
    }

    public User update(User user) {
        validate(user);
        userStorage.update(user);
        log.debug("Изменены данные по пользователю: {}", user);
        return user;
    }

    public User get(Long id) {
        log.debug("Запрошены данные по id = {}", id);
        return userStorage.get(id);
    }

    public Collection<User> getAll() {
        log.debug("Направлен запрос по всем пользователям");
        return userStorage.getAll();
    }

    public Long addFriend(Long userId, Long friendId) {
        userStorage.addFriend(userId, friendId);
        return friendId;
    }

    public Long removeFriend(Long userId, Long friendId) {
        userStorage.removeFriend(userId, friendId);
        return friendId;
    }

    public List<User> getFriends(Long userId) {
        return userStorage.getFriends(userId);
    }


    public List<User> getCommonFriends(Long userId, Long otherUserId) {
        return userStorage.getCommonFriends(userId, otherUserId);
    }

    private void validate(User user) {
        if (user.getLogin().contains(" ")) {
            log.warn("Получен некорректный логин");
            throw new ValidationException("Логин не должен содержать пробелы");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
