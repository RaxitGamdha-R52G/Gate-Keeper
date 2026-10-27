package tech.raxit.gatekeeper.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.raxit.gatekeeper.management.entity.ManagementUser;

import java.util.Optional;

public interface ManagementUserRepository extends JpaRepository<ManagementUser, Long> {

    Optional<ManagementUser> findByUsername(String username);

    Optional<ManagementUser> findByOrganizationId(Long organizationId);

    boolean existsByOrganizationId(Long organizationId);

    boolean existsByUsername(String username);
}
