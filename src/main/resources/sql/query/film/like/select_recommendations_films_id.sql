SELECT FL3.film_id
FROM FILM_LIKES AS FL3
WHERE FL3.user_id = (SELECT FL2.user_id
                     FROM FILM_LIKES AS FL2
                     WHERE FL2.user_id <> ?
                       AND FL2.film_id IN (SELECT FL1.film_id
                                           FROM FILM_LIKES AS FL1
                                           WHERE FL1.user_id = ?)
                     GROUP BY FL2.user_id
                     ORDER BY COUNT(FL2.film_id) DESC
    LIMIT 1
    )