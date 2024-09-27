SELECT f.*, m.*, COUNT(FL.user_id) AS rate
FROM FILM f
LEFT JOIN MPA_RATING M ON f.rating_MPA = M.rating_id
LEFT JOIN FILM_LIKES FL ON f.film_id = FL.film_id
WHERE f.film_id IN (SELECT film_id FROM film_director WHERE director_id = ?)
GROUP BY f.film_id
ORDER BY rate DESC