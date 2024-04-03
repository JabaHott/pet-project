package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.customExceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class UserService {
    private UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User create(User user) {
        validate(user);
        userStorage.create(user);
        log.debug("Сохранен пользователь : {}", user.toString());
        return user;
    }

    public User update(User user) {
        validate(user);
        userStorage.update(user);
        log.debug("Изменены данные по пользователю: {}", user.toString());
        return user;
    }

    public User get(Long id) {
        log.debug("Запрошены данные по id = {}", id);
        return userStorage.get(id);
    }

    public Map<Long, User> getAll() {
        log.debug("Направлен запрос по всем пользователям");
        return userStorage.getAll();
    }

    public Long addFriend(Long userId, Long friendId) {
        User user = userStorage.get(userId);
        User friend = userStorage.get(friendId);
        log.debug("Пользователь {} добавил в друзья пользователя {}", user.getId(), friendId);
        user.addFriend(friendId);
        friend.addFriend(userId);
        return friendId;
    }

    public Long removeFriend(Long userId, Long friendId) {
        User user = userStorage.get(userId);
        User friend = userStorage.get(friendId);
        log.debug("Пользователь {} добавил в друзья пользователя {}", user.getId(), friendId);
        user.removeFriend(friendId);
        friend.removeFriend(user.getId());
        return friendId;
    }

    public List<User> getFriends(Long userId) {
        List<User> friends = new ArrayList<>();
        for (Long id : userStorage.get(userId).getFriends()) {
            friends.add(userStorage.get(id));
        }
        return friends;
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

    public List<User> getCommonFriends(Long userId, Long otherUserId) {
        User user = userStorage.get(userId);
        User otherUser = userStorage.get(otherUserId);
        Set<Long> commonFriends = user.getFriendsSet();
        commonFriends.retainAll(otherUser.getFriends());
        List<User> commonFriendsList = new ArrayList<>();
        for (Long id : commonFriends) {
            commonFriendsList.add(userStorage.get(id));
        }
        return commonFriendsList;
    }
}
