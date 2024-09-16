SELECT (
           SELECT user_id
           FROM USER_FRIENDS
           WHERE user_id = ? AND friend_id = ?
       ) AND (
           SELECT friend_id
           FROM USER_FRIENDS
           WHERE user_id = ? AND friend_id = ?
       ) AS status