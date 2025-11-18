package tech.gurugram.rating_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tech.gurugram.rating_app.dto.RatingUserDto;
import tech.gurugram.rating_app.model.Rating;
import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    List<Rating> findByStoreId(Long storeId);
    Optional<Rating> findByStoreIdAndUserId(Long storeId, Long userId);

    List<Rating> findByStoreIdInAndUserId(List<Long> storeIds, Long userId);

    @Query("SELECT new tech.gurugram.rating_app.dto.RatingUserDto(r.id, r.score, r.comment, r.createdAt, u.name, u.email) " +
            "FROM Rating r JOIN r.user u " +
            "WHERE r.store.id = :storeId " +
            "ORDER BY r.createdAt DESC")
    List<RatingUserDto> findRatingsWithUserDetailsByStoreId(Long storeId);

    @Query("SELECT avg(r.score) FROM Rating r WHERE r.store.id = :storeId")
    Double getAverageRatingByStoreId(Long storeId);

    @Query("SELECT count(r) FROM Rating r WHERE r.store.id = :storeId")
    Long getRatingCountByStoreId(Long storeId);
}