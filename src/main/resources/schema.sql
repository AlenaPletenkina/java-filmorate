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
    FOREIGN KEY (film_id) REFERENCES FILM (film_id) ON DELETE CASCADE,
    FOREIGN KEY (genre_id) REFERENCES GENRES (genre_id) ON DELETE CASCADE,
    PRIMARY KEY (film_id, genre_id)
);
CREATE TABLE IF NOT EXISTS FILM_LIKES
(
    film_id INT,
    user_id INT,
    FOREIGN KEY (film_id) REFERENCES FILM (film_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES USERS (user_id) ON DELETE CASCADE,
    PRIMARY KEY (film_id, user_id)
);
CREATE TABLE IF NOT EXISTS USER_FRIENDS
(
    user_id   INT,
    friend_id INT,
    status    BOOLEAN,
    FOREIGN KEY (user_id) REFERENCES USERS (user_id) ON DELETE CASCADE,
    FOREIGN KEY (friend_id) REFERENCES USERS (user_id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, friend_id)
);
CREATE TABLE IF NOT EXISTS REVIEWS
(
    review_id   INT PRIMARY KEY AUTO_INCREMENT,
    content     VARCHAR,
    is_positive BOOLEAN, -- если false то отрицательный, если true то положительный
    user_id     INT NOT NULL ,
    film_id     INT NOT NULL ,
    useful      INT NOT NULL DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES USERS (user_id) ON DELETE CASCADE,
    FOREIGN KEY (film_id) REFERENCES FILM (film_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS REVIEWS_LIKES
(
    review_id   INT,
    user_id     INT,
    is_like     BOOLEAN, -- если false то дизлайк, если true то лайк
    PRIMARY KEY (review_id, user_id),
    FOREIGN KEY (review_id) REFERENCES REVIEWS (review_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES USERS (user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS DIRECTORS
(
    director_id INT AUTO_INCREMENT,
    name        VARCHAR(255) NOT NULL,
    PRIMARY KEY (director_id)
);

CREATE TABLE IF NOT EXISTS FILM_DIRECTOR
(
    director_id INT,
    film_id     INT,
    PRIMARY KEY (director_id, film_id),
    FOREIGN KEY (director_id) REFERENCES directors (director_id) ON DELETE CASCADE,
    FOREIGN KEY (film_id) REFERENCES film (film_id) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS EVENTS
(
    event_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    event_type VARCHAR(10) NOT NULL,
    operation VARCHAR(10) NOT NULL,
    entity_id INT NOT NULL,
    TIMESTAMP BIGINT NOT NULL,
    CONSTRAINT fk_events_users FOREIGN KEY (user_id) REFERENCES USERS(user_id) ON DELETE CASCADE
);
