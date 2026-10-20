package tech.raxit.gatekeeper.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.raxit.gatekeeper.management.entity.ManagementUser;

public interface ManagementUserRepository extends JpaRepository<ManagementUser, Long> {

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
