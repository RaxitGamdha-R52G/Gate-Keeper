package tech.raxit.gatekeeper.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.raxit.gatekeeper.management.entity.RouteConfigurationVersion;

import java.util.List;

public interface RouteConfigurationVersionRepository extends JpaRepository<RouteConfigurationVersion, Long> {

    List<RouteConfigurationVersion> findAllByOrganizationIdAndRouteIdOrderByVersionNumberDesc(Long id, String routeId);
}
