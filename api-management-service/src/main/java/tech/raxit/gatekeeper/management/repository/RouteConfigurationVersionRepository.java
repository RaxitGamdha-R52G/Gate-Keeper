package tech.raxit.gatekeeper.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.raxit.gatekeeper.management.entity.RouteConfigurationVersion;

public interface RouteConfigurationVersionRepository extends JpaRepository<RouteConfigurationVersion, Long> {
}
