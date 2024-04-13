package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.customExceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> userStorage = new HashMap<>();
    private Long userIdCounter = 0L;


    @Override
    public User create(User user) {
        Long id = ++userIdCounter;
        user.setId(id);
        userStorage.put(id, user);
        return user;
    }

    @Override
    public User update(User user) {
        if (!userStorage.containsKey(user.getId())) {
            throw new NotFoundException("Пользователь с id=" + user.getId() + " не найден");
        }
        userStorage.put(user.getId(), user);
        return user;
    }

    @Override
    public User get(Long id) {
        if (!userStorage.containsKey(id)) {
            throw new NotFoundException("Пользователь с id=" + id + " не найден");
        }
        return userStorage.get(id);
    }

    @Override
    public Collection<User> getAll() {
        return userStorage.values();
    }
}
