package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.customExceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    Map<Long, User> userMap = new HashMap<>();
    private Long userIdCounter = 1L;


    @Override
    public User create(User user) {
        user.setId(userIdCounter);
        userMap.put(userIdCounter, user);
        userIdCounter++;
        return user;
    }

    @Override
    public User update(User user) {
        if (!userMap.containsKey(user.getId())) {
            throw new NotFoundException("Пользователь с id=" + user.getId() + " не найден");
        }
        userMap.put(user.getId(), user);
        return user;
    }

    @Override
    public User get(Long id) {
        if (!userMap.containsKey(id)) {
            throw new NotFoundException("Пользователь с id=" + id + " не найден");
        }
        return userMap.get(id);
    }

    @Override
    public Map<Long, User> getAll() {
        return userMap;
    }
}
