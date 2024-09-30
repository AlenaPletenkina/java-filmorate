SELECT f.*, m.*, COUNT(fl.user_id) AS rate
FROM FILM f
LEFT JOIN MPA_RATING m ON f.rating_MPA = m.rating_id
LEFT JOIN FILM_LIKES fl ON f.film_id = fl.film_id
LEFT JOIN FILM_DIRECTOR fd ON f.film_id = fd.film_id
LEFT JOIN DIRECTORS d ON fd.director_id = d.director_id
WHERE LOWER(d.name) LIKE LOWER(?)
GROUP BY f.film_id
ORDER BY rate DESC, d.name ASC;
