package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;


import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class FilmControllerTest {

    FilmController filmController;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void emptyMessageTest() throws Exception {
        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString("{}"))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void emptyNameTest() throws Exception {
        Film film = new Film(null, "description", LocalDate.of(1996, 4, 1), 200);
        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void bigDescTest() throws Exception {
        Film film = new Film("name", "a".repeat(300), LocalDate.of(1996, 4, 1), 200);
        try {
            mockMvc.perform(
                            post("/films")
                                    .content(objectMapper.writeValueAsString(film))
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        } catch (Exception ue) {
            assertEquals("Описание не должно быть больше 200 символов", ue.getMessage().substring(119));
        }
    }

    @Test
    public void oldFilmTest() throws Exception {
        Film film = new Film("Name", "description", LocalDate.of(1580, 4, 1), 200);
        try {
            mockMvc.perform(
                            post("/films")
                                    .content(objectMapper.writeValueAsString(film))
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        } catch (Exception ue) {
            assertEquals("Дата выпуска должна быть после 28 декабря 1985 года.", ue.getMessage().substring(119));
        }
    }

    @Test
    public void negDurationTest() throws Exception {
        Film film = new Film("Name", "description", LocalDate.of(1996, 4, 1), -200);
        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void allGoodTest() throws Exception {
        Film film = new Film("Name", "description", LocalDate.of(1996, 4, 1), 200);
        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
