package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.customExceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserControllerTest {
    private final UserService userService;
    private static final Validator validator;

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

    @Test
    public void validUser() {
        User user = User.builder()
                .login("SmokeMaster")
                .name("Daite_pokurit")
                .email("qwertyuiopasd@yandex.ru")
                .birthday(LocalDate.of(2001, 4, 14))
                .build();
        userService.create(user);
        assertTrue(userService.getAll().contains(user));
    }

    @Test
    public void updUser() {
        User user = User.builder()
                .login("SmokeMaster")
                .name("Daite_pokurit")
                .email("qwertyuiopasd@yandex.ru")
                .birthday(LocalDate.of(2001, 4, 14))
                .build();
        userService.create(user);
        User user1 = User.builder()
                .id(user.getId())
                .login("SmokeMaster1")
                .name("Daite_pokurit")
                .email("qwertyuiopasd@yandex.ru")
                .birthday(LocalDate.of(2001, 4, 14))
                .build();
        userService.update(user1);
        assertTrue(userService.getAll().contains(user1));
        assertFalse(userService.getAll().contains(user));
    }

    @Test
    public void emptName() {
        User user = User.builder()
                .login("SmokeMaster")
                .name("")
                .email("qwertyuiopasd@yandex.ru")
                .birthday(LocalDate.of(2001, 4, 14))
                .build();
        userService.create(user);
        assertEquals("SmokeMaster", user.getName());
    }

    @Test
    public void notValidEmail() {
        User user = User.builder()
                .login("SmokeMaster")
                .name("Daite_pokurit")
                .email("qwertyuiopasd")
                .birthday(LocalDate.of(2001, 4, 14))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
    }

    @Test
    public void emptLogin() {
        User user = User.builder()
                .login("")
                .name("Daite_pokurit")
                .email("qwertyuiopasd@yandex.ru")
                .birthday(LocalDate.of(2001, 4, 14))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
    }

    @Test
    public void blankLogin() {
        User user = User.builder()
                .login(" ")
                .name("123ll")
                .email("qwertyuiopasd@yandex.ru")
                .birthday(LocalDate.of(2001, 4, 14))
                .build();
        assertThrows(ValidationException.class, () -> {
            userService.create(user);
        });
    }

    @Test
    public void futureUser() {
        User user = User.builder()
                .login("SmokeMaster")
                .name("Daite_pokurit")
                .email("qwertyuiopasd@yandex.ru")
                .birthday(LocalDate.of(3001, 4, 14))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
    }

    @Test
    public void addFriend() {
        User user = User.builder()
                .login("SmokeMaster")
                .name("Daite_pokurit")
                .email("qwertyuiopasd@yandex.ru")
                .birthday(LocalDate.of(2001, 4, 14))
                .build();
        userService.create(user);
        User user1 = User.builder()
                .login("SmokeMaster1")
                .name("Daite_pokurit1")
                .email("qwertyuiopasd1@yandex.ru")
                .birthday(LocalDate.of(2001, 4, 14))
                .build();
        userService.create(user1);
        userService.addFriend(user.getId(), user1.getId());
    }

    @Test
    public void deleteFriend() {
        User user = User.builder()
                .login("SmokeMaster")
                .name("Daite_pokurit")
                .email("qwertyuiopasd@yandex.ru")
                .birthday(LocalDate.of(2001, 4, 14))
                .build();
        userService.create(user);
        User user1 = User.builder()
                .login("SmokeMaster1")
                .name("Daite_pokurit1")
                .email("qwertyuiopasd1@yandex.ru")
                .birthday(LocalDate.of(2001, 4, 14))
                .build();
        userService.create(user1);
        userService.addFriend(user.getId(), user1.getId());
        userService.removeFriend(user.getId(), user1.getId());
    }

    @Test
    public void commonFriend() {
        User user = User.builder()
                .login("SmokeMaster")
                .name("Daite_pokurit")
                .email("qwertyuiopasd@yandex.ru")
                .birthday(LocalDate.of(2001, 4, 14))
                .build();
        userService.create(user);
        User friend = User.builder()
                .login("SmokeMaster1")
                .name("Daite_pokurit1")
                .email("qwertyuiopasd1@yandex.ru")
                .birthday(LocalDate.of(2001, 4, 14))
                .build();
        userService.create(friend);
        User mutualFriend = User.builder()
                .login("SmokeMaster1")
                .name("Daite_pokurit1")
                .email("qwertyuiopasd1@yandex.ru")
                .birthday(LocalDate.of(2001, 4, 14))
                .build();
        userService.create(mutualFriend);
        userService.addFriend(user.getId(), mutualFriend.getId());
        userService.addFriend(friend.getId(), mutualFriend.getId());
        List<User> mutual = userService.getCommonFriends(user.getId(), friend.getId());
        assertEquals(List.of(mutualFriend), mutual);
    }

    @Test
    public void allFriend() {
        User user = User.builder()
                .login("SmokeMaster")
                .name("Daite_pokurit")
                .email("qwertyuiopasd@yandex.ru")
                .birthday(LocalDate.of(2001, 4, 14))
                .build();
        userService.create(user);
        User friend = User.builder()
                .login("SmokeMaster1")
                .name("Daite_pokurit1")
                .email("qwertyuiopasd1@yandex.ru")
                .birthday(LocalDate.of(2001, 4, 14))
                .build();
        userService.create(friend);
        User mutualFriend = User.builder()
                .login("SmokeMaster1")
                .name("Daite_pokurit1")
                .email("qwertyuiopasd1@yandex.ru")
                .birthday(LocalDate.of(2001, 4, 14))
                .build();
        userService.create(mutualFriend);
        userService.addFriend(user.getId(), friend.getId());
        userService.addFriend(user.getId(), mutualFriend.getId());
        List<User> all = userService.getFriends(user.getId());
        assertEquals(List.of(friend, mutualFriend), all);
    }
}