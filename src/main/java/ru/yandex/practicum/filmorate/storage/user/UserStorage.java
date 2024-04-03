package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Map;

public interface UserStorage {
    User create(User user);

    User update(User user);

    User get(Long id);

    Map<Long, User> getAll();
}
