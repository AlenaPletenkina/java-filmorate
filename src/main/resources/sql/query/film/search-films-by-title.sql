SELECT f.*, m.*, COUNT(fl.user_id) AS rate
FROM FILM f
LEFT JOIN MPA_RATING m ON f.rating_MPA = m.rating_id
LEFT JOIN FILM_LIKES fl ON f.film_id = fl.film_id
WHERE LOWER(f.film_name) LIKE LOWER(?)
GROUP BY f.film_id
ORDER BY rate DESC, f.film_name ASC;
