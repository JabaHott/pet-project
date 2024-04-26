package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.customExceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;

@Component("userDbStorage")
@Slf4j
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User create(User user) {
        String sqlQuery = "INSERT INTO PUBLIC.\"USER\" (USER_EMAIL, USER_LOGIN, USER_NAME, USER_BIRTHDAY) VALUES(?, ?, ?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setObject(4, java.sql.Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);
        if (keyHolder.getKey() != null) {
            user.setId(keyHolder.getKey().longValue());
            return user;
        } else {
            return null;
        }
    }

    @Override
    public User update(User user) {
        if (!exists(user.getId())) {
            throw new NotFoundException("Пользователь с id = " + user.getId() + " не найден");
        }
        String sqlQuery = "UPDATE PUBLIC.\"USER\" SET USER_EMAIL=?, USER_LOGIN=?, USER_NAME=?, USER_BIRTHDAY=? WHERE USER_ID=?;";
        jdbcTemplate.update(sqlQuery, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return user;
    }

    @Override
    public User get(Long id) {
        if (!exists(id)) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        String sqlQuery = "SELECT USER_ID, USER_EMAIL, USER_LOGIN, USER_NAME, USER_BIRTHDAY FROM PUBLIC.\"USER\" WHERE USER_ID = ?;";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sqlQuery, id);
        rs.next();//аналогично фильму
        User user = User.builder()
                .id(rs.getLong("USER_ID"))
                .email(rs.getString("USER_EMAIL"))
                .login(rs.getString("USER_LOGIN"))
                .name(rs.getString("USER_NAME"))
                .birthday(rs.getDate("USER_BIRTHDAY").toLocalDate())
                .build();
        return user;
    }

    @Override
    public Collection<User> getAll() {
        String sqlQuery = "SELECT * FROM \"USER\"";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    @Override
    public Long addFriend(Long userId, Long friendId) {
        get(userId);
        get(friendId);
        String sqlQuery = "INSERT INTO PUBLIC.FRIEND (FRIEND_USER_ID, FRIEND_FRIEND_ID, STATUS) VALUES(?,?,?);";
        if (isFriends(userId, friendId)) {
            String sqlQueryForFriends = "UPDATE PUBLIC.FRIEND SET STATUS=? WHERE FRIEND_USER_ID=? AND FRIEND_FRIEND_ID=?;";
            jdbcTemplate.update(sqlQuery, userId, friendId, "Подтверждена");
            jdbcTemplate.update(sqlQueryForFriends, "Подтверждена", friendId, userId);
        } else {
            jdbcTemplate.update(sqlQuery, userId, friendId, "Не подтверждена");
        }
        return userId;
    }

    @Override
    public Long removeFriend(Long userId, Long friendId) {
        get(friendId);
        get(userId);
        String sqlQuery = "DELETE FROM PUBLIC.FRIEND WHERE FRIEND_USER_ID=? AND FRIEND_FRIEND_ID=?;";
        if (isFriends(userId, friendId)) {
            String sqlQueryIfApproved = "UPDATE PUBLIC.FRIEND SET STATUS=? WHERE FRIEND_USER_ID=? AND FRIEND_FRIEND_ID=?";
            jdbcTemplate.update(sqlQueryIfApproved, friendId, userId, "Не подтверждена");
        }
        jdbcTemplate.update(sqlQuery, userId, friendId);
        return userId;
    }

    @Override
    public List<User> getFriends(Long userId) {
        get(userId);
        String sqlQuery = "SELECT * FROM \"USER\" AS U WHERE U.USER_ID IN(SELECT FRIEND_FRIEND_ID FROM PUBLIC.FRIEND WHERE FRIEND_USER_ID = ?);";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, userId);
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long otherUserId) {
        get(userId);
        get(otherUserId);
        String sqlQuery = "SELECT * FROM \"USER\" AS U \n" +
                "WHERE U.USER_ID IN \n" +
                "(SELECT F.FRIEND_FRIEND_ID\n" +
                "FROM FRIEND AS F \n" +
                "WHERE F.FRIEND_USER_ID = ?\n" +
                "INTERSECT SELECT F.FRIEND_FRIEND_ID FROM FRIEND AS F \n" +
                "WHERE F.FRIEND_USER_ID = ?);";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, userId, otherUserId);
    }

    private boolean exists(Long id) {
        String sqlQuery = "select count(*) from \"USER\" where USER_ID = ?";
        int result = jdbcTemplate.queryForObject(sqlQuery, Integer.class, id);
        return result == 1;
    }

    private boolean isFriends(Long userId, Long friendId) {
        String sqlQuery = "SELECT COUNT(*) FROM PUBLIC.FRIEND WHERE FRIEND_USER_ID = ? AND FRIEND_FRIEND_ID = ?;";
        int res = jdbcTemplate.queryForObject(sqlQuery, Integer.class, friendId, userId);
        return res == 1;
    }

    private User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        User user = User.builder()
                .id(rs.getLong("USER_ID"))
                .email(rs.getString("USER_EMAIL"))
                .login(rs.getString("USER_LOGIN"))
                .name(rs.getString("USER_NAME"))
                .birthday(rs.getDate("USER_BIRTHDAY").toLocalDate())
                .build();
        return user;
    }
}
