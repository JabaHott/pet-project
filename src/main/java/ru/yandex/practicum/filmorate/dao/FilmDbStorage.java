package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.customExceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Component("filmDbStorage")
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserStorage userStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, @Qualifier("userDbStorage") UserStorage userStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userStorage = userStorage;
    }

    @Override
    public Film create(Film film) {
        String sqlQuery = "INSERT INTO PUBLIC.FILM (NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING, MPA_ID) VALUES(?, ?, ?, ?, ?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setObject(3, java.sql.Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getRate());
            ps.setLong(6, film.getMpa().getId());
            return ps;
        }, keyHolder);
        film.setId(keyHolder.getKey().longValue());
        genreTackle(film);
        return film;
    }

    @Override
    public Film get(Long id) {
        String sqlQuery = "SELECT * FROM FILM AS F JOIN MPA AS R ON F.MPA_ID = R.MPA_ID WHERE FILM_ID = ?;";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (!exists(id)) {
            filmRows.beforeFirst();
            log.warn("Фильм с id = {} не найден", id);
            throw new NotFoundException("Фильм с id =" + id);
        }
        filmRows.next();
        Film film = Film.builder()
                .id(filmRows.getLong("FILM_ID"))
                .name(filmRows.getString("NAME"))
                .description(filmRows.getString("DESCRIPTION"))
                .releaseDate(filmRows.getDate("RELEASE_DATE").toLocalDate())
                .duration(filmRows.getInt("DURATION"))
                .rate(filmRows.getInt("RATING"))
                .mpa(new Mpa(filmRows.getLong("MPA_ID"), filmRows.getString("MPA_NAME")))
                .build();
        List<Genre> filmGenres = mapRowToFilmGenre(id);
        List<Long> filmLikes = mapRowToLikes(id);
        film.getGenres().addAll(filmGenres);
        film.getLikes().addAll(filmLikes);
        log.info("Фильм с id = {} найден", id);
        return film;
    }

    @Override
    public Film update(Film film) {
        if (!exists(film.getId())) {
            log.warn("Фильм с id = {} не найден", film.getId());
            throw new NotFoundException("Фильм с id =" + film.getId());
        }
        String sqlQuery = "UPDATE PUBLIC.FILM SET NAME=?, DESCRIPTION=?, RELEASE_DATE=?, DURATION=?, RATING=?, MPA_ID=? WHERE FILM_ID=?;";
        jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getRate(), film.getMpa().getId(), film.getId());
        genreTackle(film);
        film.getGenres().clear();
        film.getGenres().addAll(mapRowToFilmGenre(film.getId()));
        return film;
    }

    @Override
    public Collection<Film> getAll() {
        String sqlQuery = "SELECT * FROM FILM AS F JOIN MPA AS M ON F.MPA_ID = M.MPA_ID";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public Long addLike(Long filmId, Long userId) {
        if (!likeExist(filmId, userId)) {
            String sqlQuery = "INSERT INTO FILM_LIKE(FILM_ID, USER_ID) VALUES(?,?)";
            jdbcTemplate.update(sqlQuery, filmId, userId);
        }

        return filmId;
    }

    @Override
    public Long removeLike(Long filmId, Long userId) {
        Film film1 = get(filmId);
        String sqlQuery = "DELETE FROM PUBLIC.FILM_LIKE WHERE FILM_ID=? AND USER_ID=?;";
        jdbcTemplate.update(sqlQuery, filmId, userId);
        return filmId;
    }

    public boolean exists(Long id) {
        String sqlQuery = "select count(*) from FILM where FILM_ID = ?";
        int result = jdbcTemplate.queryForObject(sqlQuery, Integer.class, id);
        return result == 1;
    }

    public boolean likeExist(Long filmId, Long userId) {
        String sqlQuery = "select count(*) from FILM_LIKE where FILM_ID = ? AND USER_ID = ?";
        int result = jdbcTemplate.queryForObject(sqlQuery, Integer.class, filmId, userId);
        return result == 1;
    }

    private Genre mapRowToGenre(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(rs.getLong("GENRE_ID"), rs.getString("GENRE_NAME"));
    }

    private List<Genre> mapRowToFilmGenre(Long filmId) {
        String sqlQuery = "SELECT FG.FILM_ID, FG.GENRE_ID, G.GENRE_NAME FROM FILM_GENRE FG JOIN GENRE G ON FG.GENRE_ID = G.GENRE_ID WHERE FG.FILM_ID = ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre, filmId);
    }

    private Long mapRowToLike(ResultSet rs, int rowNum) throws SQLException {
        return rs.getLong("USER_ID");
    }

    private List<Long> mapRowToLikes(Long filmId) {
        String sqlQuery = "SELECT USER_ID FROM FILM_LIKE WHERE FILM_ID = ?;";
        return jdbcTemplate.query(sqlQuery, this::mapRowToLike, filmId);
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        Film film = Film.builder()
                .id(rs.getLong("FILM_ID"))
                .name(rs.getString("NAME"))
                .description(rs.getString("DESCRIPTION"))
                .releaseDate(rs.getDate("RELEASE_DATE").toLocalDate())
                .duration(rs.getInt("DURATION"))
                .rate(rs.getInt("RATING"))
                .mpa(new Mpa(rs.getLong("MPA_ID"), rs.getString("MPA_NAME")))
                .build();
        List<Genre> filmGenres = mapRowToFilmGenre(film.getId());
        List<Long> filmLikes = mapRowToLikes(film.getId());
        film.getGenres().addAll(filmGenres);
        film.getLikes().addAll(filmLikes);
        return film;
    }

    private void genreTackle(Film film) {
        final Long filmId = film.getId();
        jdbcTemplate.update("DELETE FROM PUBLIC.FILM_GENRE WHERE FILM_ID=?;", filmId);
        final Set<Genre> genres = film.getGenres();
        if (genres == null || genres.isEmpty()) {
            return;
        }
        final ArrayList<Genre> genreList = new ArrayList<>(genres);
        jdbcTemplate.batchUpdate(
                "INSERT INTO PUBLIC.FILM_GENRE (FILM_ID, GENRE_ID) VALUES(?,?);",
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setLong(1, filmId);
                        ps.setLong(2, genreList.get(i).getId());
                    }

                    public int getBatchSize() {
                        return genreList.size();
                    }
                });
    }
}
