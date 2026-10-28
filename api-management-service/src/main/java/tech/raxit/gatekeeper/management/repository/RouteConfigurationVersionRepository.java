package tech.raxit.gatekeeper.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.raxit.gatekeeper.management.entity.RouteConfigurationVersion;

import java.util.List;

public interface RouteConfigurationVersionRepository extends JpaRepository<RouteConfigurationVersion, Long> {

    List<RouteConfigurationVersion> findAllByOrganizationIdAndRouteIdOrderByVersionNumberDesc(Long id, String routeId);

    @Query("""
            SELECT COALESCE(MAX(v.versionNumber), 0)
            FROM RouteConfigurationVersion v
            WHERE v.organization.id = :organizationId
              AND v.routeId = :routeId
            """)
    long findMaxVersionNumber(
            @Param("organizationId") Long organizationId,
            @Param("routeId") String routeId
    );
}
