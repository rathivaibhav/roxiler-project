package tech.gurugram.rating_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tech.gurugram.rating_app.model.Store;
import java.util.List;

public interface StoreRepository extends JpaRepository<Store, Long>, JpaSpecificationExecutor<Store> {
    List<Store> findByOwnerId(Long ownerId);
}