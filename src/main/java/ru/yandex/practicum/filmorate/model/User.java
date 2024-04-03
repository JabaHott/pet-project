package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.yandex.practicum.filmorate.controller.UserControllerInterface;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    @Null(groups = UserControllerInterface.Create.class)
    @NotNull(groups = UserControllerInterface.Update.class)
    private Long id;
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String login;
    private String name;
    @NotNull
    @PastOrPresent
    @JsonFormat
    private LocalDate birthday;
    @NotNull(groups = UserControllerInterface.Create.class)
    private Set<Long> friendsSet;
    private Set<Long> likedFilms;

    public User(String email, String login, String name, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
        this.likedFilms = new HashSet<>();
        this.friendsSet = new HashSet<>();
    }

    public User() {
        this.likedFilms = new HashSet<>();
        this.friendsSet = new HashSet<>();
    }

    public void addFriend(Long friendId) {
        friendsSet.add(friendId);
    }

    public void removeFriend(Long friendId) {
        friendsSet.remove(friendId);
    }

    public Set<Long> getFriends() {
        return friendsSet;
    }

    public void addLikedFilm(Long id) {
        likedFilms.add(id);
    }

    public void removeLikedFilm(Long id) {
        likedFilms.remove(id);
    }

}
