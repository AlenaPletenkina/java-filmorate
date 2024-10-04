package ru.yandex.practicum.filmorate.dao.review;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.utill.UtilReader;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@Repository
@Slf4j
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;

    private static final String R_SQL_QUERY_DIR = "src/main/resources/sql/query/review/";
    private static final String R_INSERT_SQL_QUERY = UtilReader.readString(R_SQL_QUERY_DIR + "insert.sql");
    private static final String R_SELECT_SQL_QUERY = UtilReader.readString(R_SQL_QUERY_DIR + "select.sql");
    private static final String R_SELECT_BY_REVIEW_ID_SQL_QUERY = UtilReader.readString(
            R_SQL_QUERY_DIR + "select_by_review_id.sql");
    private static final String R_SELECT_BY_FILM_ID_SQL_QUERY = UtilReader.readString(
            R_SQL_QUERY_DIR + "select_by_film_id.sql");
    private static final String R_UPDATE_SQL_QUERY = UtilReader.readString(R_SQL_QUERY_DIR + "update.sql");
    private static final String R_DELETE_SQL_QUERY = UtilReader.readString(R_SQL_QUERY_DIR + "delete.sql");

    private static final String L_SQL_QUERY_DIR = "src/main/resources/sql/query/review/like/";
    private static final String L_INSERT_SQL_QUERY = UtilReader.readString(L_SQL_QUERY_DIR + "insert.sql");
    private static final String L_DELETE_SQL_QUERY = UtilReader.readString(L_SQL_QUERY_DIR + "delete.sql");

    private static final String U_SQL_QUERY_DIR = "src/main/resources/sql/query/review/useful/";
    private static final String U_UPDATE_INCREASE_SQL_QUERY = UtilReader.readString(
            U_SQL_QUERY_DIR + "update_increase.sql");
    private static final String U_UPDATE_DECREASE_SQL_QUERY = UtilReader.readString(
            U_SQL_QUERY_DIR + "update_decrease.sql");

    @Autowired
    public ReviewDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Review createReview(Review review) {
        log.info("Приступаю к созданию отзыва");
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(R_INSERT_SQL_QUERY, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, review.getContent());
            stmt.setBoolean(2, review.getIsPositive());
            stmt.setInt(3, review.getUserId());
            stmt.setInt(4, review.getFilmId());
            stmt.setInt(5, review.getUseful());
            return stmt;
        }, keyHolder);
        Review reviewById = getReviewById((Integer) keyHolder.getKey());
        log.info("Создал отзыв {}", reviewById);
        return reviewById;
    }

    @Override
    public List<Review> getReview(Integer count) {
        return jdbcTemplate.query(R_SELECT_SQL_QUERY, ReviewDbStorage::makeReview, count);
    }

    @Override
    public Review getReviewById(Integer reviewId) {
        List<Review> review = jdbcTemplate.query(R_SELECT_BY_REVIEW_ID_SQL_QUERY, ReviewDbStorage::makeReview, reviewId);

        return review.stream().findAny().orElse(null);
    }

    @Override
    public List<Review> getReviewByFilmId(Integer filmId, Integer count) {
        return jdbcTemplate.query(R_SELECT_BY_FILM_ID_SQL_QUERY, ReviewDbStorage::makeReview, filmId, count);
    }

    @Override
    public Review updateReview(Review review) {
        jdbcTemplate.update(R_UPDATE_SQL_QUERY,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId());

        return getReviewById(review.getReviewId());
    }

    @Override
    public void deleteReviewById(Integer reviewId) {
        jdbcTemplate.update(R_DELETE_SQL_QUERY, reviewId);
    }

    @Override
    public void likeReview(Integer reviewId, Integer userId) {
        deleteDislikeReview(reviewId, userId);
        jdbcTemplate.update(L_INSERT_SQL_QUERY, reviewId, userId, true);

        jdbcTemplate.update(U_UPDATE_INCREASE_SQL_QUERY, reviewId);
    }

    @Override
    public void dislikeReview(Integer reviewId, Integer userId) {
        deleteLikeReview(reviewId, userId);
        jdbcTemplate.update(L_INSERT_SQL_QUERY, reviewId, userId, false);

        jdbcTemplate.update(U_UPDATE_DECREASE_SQL_QUERY, reviewId);
    }

    @Override
    public void deleteLikeReview(Integer reviewId, Integer userId) {

        int update = jdbcTemplate.update(L_DELETE_SQL_QUERY, reviewId, userId);
        if (update == 1) {
            jdbcTemplate.update(U_UPDATE_DECREASE_SQL_QUERY, reviewId);
        }
    }

    @Override
    public void deleteDislikeReview(Integer reviewId, Integer userId) {
        int update = jdbcTemplate.update(L_DELETE_SQL_QUERY, reviewId, userId);
        if (update == 1) {
            jdbcTemplate.update(U_UPDATE_INCREASE_SQL_QUERY, reviewId);
        }
    }

    @Override
    public boolean contains(Integer reviewId) {
        return getReviewById(reviewId) != null;
    }

    static Review makeReview(ResultSet resultSet, int rowNum) throws SQLException {
        return Review.builder()
                .reviewId(resultSet.getInt("review_id"))
                .content(resultSet.getString("content"))
                .isPositive(resultSet.getBoolean("is_positive"))
                .userId(resultSet.getInt("user_id"))
                .filmId(resultSet.getInt("film_id"))
                .useful(resultSet.getInt("useful"))
                .build();
    }
}