package tech.gurugram.rating_app.repository;

import tech.gurugram.rating_app.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByStoreId(Long storeId);
    List<Rating> findByUserId(Long userId);
}
