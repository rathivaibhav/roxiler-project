package tech.gurugram.rating_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import tech.gurugram.rating_app.model.Rating;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    // find all ratings for a store
    List<Rating> findByStoreId(Long storeId);
    List<Rating> findByUserId(Long userId);

    // average score of store
    @Query("SELECT AVG(r.score) FROM Rating r WHERE r.store.id = :storeId")
    Double avgScoreForStore(@Param("storeId") Long storeId);
}
