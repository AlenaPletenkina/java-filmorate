SELECT FILM.*, MPA_RATING.*, COUNT(FILM_LIKES.user_id) AS rate
FROM FILM
LEFT JOIN MPA_RATING ON FILM.rating_MPA = MPA_RATING.rating_id
LEFT JOIN FILM_LIKES ON FILM.film_id = FILM_LIKES.film_id
GROUP BY FILM.film_id
ORDER BY FILM.film_id