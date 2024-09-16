CREATE TABLE IF NOT EXISTS MPA_RATING
(
    rating_id   INT PRIMARY KEY AUTO_INCREMENT,
    rating_name VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS GENRES
(
    genre_id   INT PRIMARY KEY AUTO_INCREMENT,
    genre_name VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS FILM
(
    film_id      INT PRIMARY KEY AUTO_INCREMENT,
    film_name    VARCHAR(255) NOT NULL,
    description  VARCHAR(255),
    release_date DATE,
    duration     INT,
    rating_MPA   INT REFERENCES MPA_RATING (rating_id)
);

CREATE TABLE IF NOT EXISTS USERS
(
    user_id   INT PRIMARY KEY AUTO_INCREMENT,
    email     VARCHAR(255) NOT NULL UNIQUE,
    login     VARCHAR(255) NOT NULL UNIQUE,
    user_name VARCHAR(255) NOT NULL,
    birthday  DATE
);
CREATE TABLE IF NOT EXISTS FILM_GENRES
(
    film_id  INT,
    genre_id INT,
    FOREIGN KEY (film_id) REFERENCES FILM (film_id),
    FOREIGN KEY (genre_id) REFERENCES GENRES (genre_id),
    PRIMARY KEY (film_id, genre_id)
);
CREATE TABLE IF NOT EXISTS FILM_LIKES
(
    film_id INT,
    user_id INT,
    FOREIGN KEY (film_id) REFERENCES FILM (film_id),
    FOREIGN KEY (user_id) REFERENCES USERS (user_id),
    PRIMARY KEY (film_id, user_id)
);
CREATE TABLE IF NOT EXISTS USER_FRIENDS
(
    user_id   INT,
    friend_id INT,
    status    BOOLEAN,
    FOREIGN KEY (user_id) REFERENCES USERS (user_id),
    FOREIGN KEY (friend_id) REFERENCES USERS (user_id),
    PRIMARY KEY (user_id, friend_id)
);

