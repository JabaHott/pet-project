package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.customExceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component("inMemoryUserStorage")
@Slf4j
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

    @Override
    public Long addFriend(Long userId, Long friendId) {
        User user = userStorage.get(userId);
        User friend = userStorage.get(friendId);
        log.debug("Пользователь {} добавил в друзья пользователя {}", user.getId(), friendId);
        user.addFriend(friendId);
        friend.addFriend(userId);
        return friendId;
    }

    @Override
    public Long removeFriend(Long userId, Long friendId) {
        User user = userStorage.get(userId);
        User friend = userStorage.get(friendId);
        log.debug("Пользователь {} добавил в друзья пользователя {}", user.getId(), friendId);
        user.removeFriend(friendId);
        friend.removeFriend(user.getId());
        return friendId;
    }

    @Override
    public List<User> getFriends(Long userId) {
        List<User> friends = new ArrayList<>();
        for (Long id : userStorage.get(userId).getFriends()) {
            friends.add(userStorage.get(id));
        }
        return friends;
    }

    @Override
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
