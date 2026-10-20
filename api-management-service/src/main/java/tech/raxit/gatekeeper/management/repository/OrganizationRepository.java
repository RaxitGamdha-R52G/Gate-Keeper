package tech.raxit.gatekeeper.management.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import tech.raxit.gatekeeper.management.entity.Organization;

import java.util.Optional;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    boolean existsByName(String name);

    boolean existsBySlug(String slug);

    Optional<Organization> findByIdAndDeletedAtIsNull(Long id);

    Page<Organization> findAllByDeletedAtIsNull(Pageable pageable);
}
