SELECT FL.FILM_ID AS film_id,           -- идентификатор фильма
       FL.FILM_NAME AS film_name,                    -- название
       FL.DESCRIPTION AS description,      -- описание
       FL.RELEASE_DATE AS release_date,    -- дата релиза
       FL.DURATION AS duration,  -- продолжительность в минутах
       FL.RATING_MPA AS rating_id,  -- идентификатор рейтинга MPA
       M.RATING_NAME AS rating_name,      -- наименование рейтинга MPA
       GE.GENRE_ID AS GENRE_ID,            -- идентификатор жанра
       GE.GENRE_NAME AS GENRE_NAME,        -- название жанра
       (SELECT COUNT(*)
        FROM FILM_LIKES
        WHERE FILM_LIKES.FILM_ID = FL.FILM_ID
           ) AS rate                           -- количество лайков у фильма

FROM FILM AS FL                        -- таблица с фильмами
         LEFT JOIN MPA_RATING AS M ON FL.RATING_MPA = M.RATING_ID -- связь таблиц Фильмы -< MPA
         LEFT JOIN FILM_GENRES AS FG ON FL.FILM_ID = FG.FILM_ID          -- связь с таблицей составных ключей Жанры >-< Фильмы
         LEFT JOIN GENRES AS GE ON FG.GENRE_ID = GE.GENRE_ID             -- связь таблиц между FILMS(фильмы) и GENRE(жанры)
         LEFT JOIN FILM_LIKES AS LF ON FL.FILM_ID = LF.FILM_ID           -- связь с таблицей составных ключей Фильмы >-< User

WHERE FL.FILM_ID IN (SELECT F.FILM_ID -- Лимит фильмов
                     FROM FILM F
                              LEFT JOIN FILM_LIKES AS FI_LI ON F.FILM_ID = FI_LI.FILM_ID
                     GROUP BY F.FILM_ID
                     ORDER BY COUNT(FI_LI.USER_ID) DESC, F.FILM_ID
                     LIMIT ?)

GROUP BY GENRE_NAME, FL.FILM_ID
ORDER BY rate DESC, film_id, GENRE_NAME;                 -- количество выгружаемых записей