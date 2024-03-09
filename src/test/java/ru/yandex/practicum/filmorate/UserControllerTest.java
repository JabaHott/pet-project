package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.customExceptions.UserValidationException;
import ru.yandex.practicum.filmorate.model.User;


import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class UserControllerTest {
    UserController userController;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void emptyUserBodyTest() throws Exception {
        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString("{}"))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void validUserTest() throws Exception {
        LocalDate birthday = LocalDate.of(2000, 4, 14);
        User user = new User("yura@mail.ru", "baiden_loh", "Russkii", birthday);
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    public void invalidEmailNoDogTest() throws Exception {
        LocalDate birthday = LocalDate.of(2000, 4, 14);
        User user = new User("yura", "baiden_loh", "Russkii", birthday);
        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void emptyEmailTest() throws Exception {
        LocalDate birthday = LocalDate.of(2000, 4, 14);
        User user = new User(null, "baiden_loh", "Russkii", birthday);
        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void loginWhiteSpacesTest() throws Exception {
        LocalDate birthday = LocalDate.of(2000, 4, 14);
        User user = new User("yura@mail.ru", "baiden loh", "Russkii", birthday);
        try {
            mockMvc.perform(
                    post("/users")
                            .content(objectMapper.writeValueAsString(user))
                            .contentType(MediaType.APPLICATION_JSON));
        } catch (Exception ue) {
            assertEquals("Логин не должен быть пустой и не должен содержать пробелы", ue.getMessage().substring(119));
        } // как это лучше проверить не знаю. По идее нужно что-то, что будет обрабатывать ошибки и выбрасывать как validate, но я не смог разобраться что
    }

    @Test
    public void emptyLoginTest() throws Exception {
        LocalDate birthday = LocalDate.of(2000, 4, 14);
        User user = new User("yura@mail.ru", null, "Russkii", birthday);
        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void futureBirthdayTest() throws Exception {
        LocalDate birthday = LocalDate.of(2026, 4, 14);
        User user = new User("yura@mail.ru", "baiden_loh", "Russkii", birthday);
        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void emptyNameTest() throws Exception {
        LocalDate birthday = LocalDate.of(2001, 4, 14);
        User user = new User("yura@mail.ru", "baiden_loh", "", birthday);
        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
