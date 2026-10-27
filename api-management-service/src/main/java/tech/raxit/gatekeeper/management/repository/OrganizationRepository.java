package tech.raxit.gatekeeper.management.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Range;
import org.springframework.data.jpa.repository.JpaRepository;
import tech.raxit.gatekeeper.management.entity.Organization;
import tech.raxit.gatekeeper.management.entity.enums.OrganizationStatus;

import java.util.Optional;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsBySlugIgnoreCase(String slug);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long currentId);

    boolean existsBySlugIgnoreCaseAndIdNot(String slug, Long currentId);

    Page<Organization> findAllByStatusNot(OrganizationStatus status, Pageable pageable);

    Optional<Organization> findByIdAndStatusNot(Long id, OrganizationStatus status);
}
