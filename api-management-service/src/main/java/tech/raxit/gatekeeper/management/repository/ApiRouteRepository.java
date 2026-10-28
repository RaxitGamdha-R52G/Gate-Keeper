package tech.raxit.gatekeeper.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.raxit.gatekeeper.management.entity.ApiRoute;
import tech.raxit.gatekeeper.management.entity.enums.OrganizationStatus;

import java.util.List;
import java.util.Optional;

public interface ApiRouteRepository extends JpaRepository<ApiRoute, Long> {

    List<ApiRoute> findAllByOrganizationId(Long organizationId);

    List<ApiRoute> findAllByActiveTrueAndOrganizationStatus(OrganizationStatus status);

    Optional<ApiRoute> findByIdAndOrganizationId(Long id, Long organizationId);

    boolean existsByOrganizationIdAndRouteId(Long organizationId, String routeId);
}
