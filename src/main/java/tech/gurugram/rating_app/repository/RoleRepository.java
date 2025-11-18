package tech.gurugram.rating_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.gurugram.rating_app.model.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
