DROP TABLE IF EXISTS PUBLIC."FILM" CASCADE;
DROP TABLE IF EXISTS PUBLIC."GENRE" CASCADE;
DROP TABLE IF EXISTS PUBLIC."FILM_GENRE" CASCADE;
DROP TABLE IF EXISTS PUBLIC."MPA" CASCADE;
DROP TABLE IF EXISTS PUBLIC."LIKE" CASCADE;
DROP TABLE IF EXISTS PUBLIC."USER" CASCADE;
DROP TABLE IF EXISTS PUBLIC."FRIEND" CASCADE;

CREATE TABLE IF NOT EXISTS PUBLIC."USER" (
  USER_ID INTEGER NOT NULL AUTO_INCREMENT,
  USER_EMAIL VARCHAR NOT NULL,
  USER_LOGIN VARCHAR NOT NULL,
  USER_NAME VARCHAR NOT NULL,
  USER_BIRTHDAY DATE NOT NULL,
  CONSTRAINT USER_PK PRIMARY KEY (USER_ID)
);

CREATE TABLE IF NOT EXISTS PUBLIC.FRIEND (
  FRIEND_USER_ID INTEGER NOT NULL,
  FRIEND_FRIEND_ID INTEGER NOT NULL,
  STATUS VARCHAR NOT NULL,
  CONSTRAINT FRIEND_PK PRIMARY KEY (FRIEND_USER_ID, FRIEND_FRIEND_ID),
  CONSTRAINT FRIEND_FK FOREIGN KEY (FRIEND_USER_ID) REFERENCES PUBLIC."USER"(USER_ID) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT FRIEND_FK1 FOREIGN KEY (FRIEND_FRIEND_ID) REFERENCES PUBLIC."USER"(USER_ID) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS PUBLIC.GENRE (
  GENRE_ID INTEGER NOT NULL AUTO_INCREMENT,
  GENRE_NAME VARCHAR NOT NULL ,
  CONSTRAINT GENRE_PK PRIMARY KEY (GENRE_ID)
);

CREATE TABLE IF NOT EXISTS PUBLIC.MPA (
  MPA_ID INTEGER NOT NULL AUTO_INCREMENT,
  MPA_NAME VARCHAR NOT NULL,
  CONSTRAINT MPA_PK PRIMARY KEY (MPA_ID)
);

CREATE TABLE IF NOT EXISTS PUBLIC.FILM (
  FILM_ID INTEGER NOT NULL AUTO_INCREMENT,
  NAME VARCHAR NOT NULL,
  DESCRIPTION VARCHAR(200) NOT NULL,
  RELEASE_DATE DATE NOT NULL,
  DURATION INTEGER NOT NULL,
  RATING INTEGER NOT NULL,
  MPA_ID INTEGER NOT NULL,
  CONSTRAINT FILM_PK PRIMARY KEY (FILM_ID),
  CONSTRAINT FILM_FK FOREIGN KEY (MPA_ID) REFERENCES PUBLIC.MPA(MPA_ID) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS PUBLIC.FILM_GENRE (
  FILM_ID INTEGER,
  GENRE_ID INTEGER,
  CONSTRAINT FILM_GENRE_PK PRIMARY KEY (FILM_ID, GENRE_ID),
  CONSTRAINT FILM_GENRE_FK FOREIGN KEY (FILM_ID) REFERENCES PUBLIC.FILM(FILM_ID) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT FILM_GENRE_FK1 FOREIGN KEY (GENRE_ID) REFERENCES PUBLIC.GENRE(GENRE_ID) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS PUBLIC.FILM_LIKE (
  FILM_ID INTEGER NOT NULL,
  USER_ID INTEGER NOT NULL ,
  CONSTRAINT LIKE_FK FOREIGN KEY (FILM_ID) REFERENCES PUBLIC.FILM(FILM_ID) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT LIKE_FK1 FOREIGN KEY (USER_ID) REFERENCES PUBLIC."USER"(USER_ID) ON DELETE CASCADE ON UPDATE CASCADE
);

 INSERT INTO PUBLIC.MPA(MPA_NAME) VALUES ('G'),
    	('PG'),
	    ('PG-13'),
	    ('R'),
	    ('NC-17');

INSERT INTO PUBLIC.GENRE
	(GENRE_NAME) VALUES ('Комедия'),
                  ('Драма'),
                  ('Мультфильм'),
                  ('Триллер'),
                  ('Документальный'),
                  ('Боевик');