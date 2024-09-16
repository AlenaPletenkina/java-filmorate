SELECT GENRES.genre_id, genre_name
FROM FILM_GENRES
         LEFT JOIN GENRES ON FILM_GENRES.genre_id = GENRES.genre_id
WHERE film_id = ?
ORDER BY GENRES.genre_id