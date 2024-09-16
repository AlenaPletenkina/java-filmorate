INSERT INTO FILM (film_name, description, release_date, duration, rating_MPA)
VALUES ('Собака-кусака',
        'Про самого отважного пса',
        '1969-04-28',
        100,
        3),
       ('Гарри Поттер',
        'Про юного волшебника',
        '2001-12-13',
        178,
        3),
       ('Криминальное чтиво',
        'Обзорная экскурсия по сетям общественного питания во Франции',
        '1994-05-21',
        154,
        4);

INSERT INTO USERS (email, login, user_name, birthday)
VALUES ('trauma@email.xyz',
        'trauma',
        'Robert',
        '1995-10-02'),
       ('example@example.biz',
        'example',
        'EXAMPLE',
        '2001-01-01'),
       ('tructornatto@otan.geo',
        'anz',
        'Ans',
        '1960-11-12');

INSERT INTO FILM_LIKES (film_id, user_id)
VALUES (1, 1),
       (2, 1),
       (2, 2),
       (3, 1),
       (3, 2),
       (3, 3);

INSERT INTO USER_FRIENDS (user_id, friend_id, status)
VALUES (1, 2, TRUE),
       (2, 1, TRUE),
       (2, 3, TRUE),
       (3, 1, DEFAULT),
       (3, 2, TRUE);

INSERT INTO FILM_GENRES (film_id, genre_id)
VALUES (1, 1),
       (2, 2),
       (3, 1),
       (3, 4);