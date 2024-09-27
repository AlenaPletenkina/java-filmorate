SELECT f.*
FROM FILM f
JOIN FILM_DIRECTOR fd ON f.film_id = fd.film_id
JOIN DIRECTORS d ON fd.director_id = d.director_id
WHERE LOWER(d.name)
LIKE ?