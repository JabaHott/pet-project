package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.customExceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> getAllMpa() {
        String sqlQuery = "SELECT MPA_ID, MPA_NAME FROM PUBLIC.MPA;";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre);
    }

    @Override
    public Mpa getMpaById(Long id) {
        String sqlQuery = "SELECT MPA_ID, MPA_NAME FROM PUBLIC.MPA WHERE MPA_ID = ?;";
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (genreRows.next()) {
            Mpa mpa = new Mpa(genreRows.getLong("MPA_ID"), genreRows.getString("MPA_NAME"));
            log.info("Найдет mpa с id = {}", id);
            return mpa;
        }
        log.warn("Не найден mpa с id = {}", id);
        throw new NotFoundException("Mpa с id" + id + "не найден!");
    }

    private Mpa mapRowToGenre(ResultSet rs, int rowNum) throws SQLException {
        return new Mpa(rs.getLong("MPA_ID"), rs.getString("MPA_NAME"));
    }
}
