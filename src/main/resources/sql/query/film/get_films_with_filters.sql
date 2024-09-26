SELECT f.*, COUNT(fl.user_id) AS likes_count
FROM film f
LEFT JOIN film_likes fl ON f.film_id = fl.film_id
LEFT JOIN film_genres fg ON f.film_id = fg.film_id
LEFT JOIN genres g ON fg.genre_id = g.genre_id
WHERE (:genreId IS NULL OR g.genre_id = :genreId)
AND (:year IS NULL OR EXTRACT(YEAR FROM f.release_date) = :year)
GROUP BY f.film_id
ORDER BY likes_count DESC;