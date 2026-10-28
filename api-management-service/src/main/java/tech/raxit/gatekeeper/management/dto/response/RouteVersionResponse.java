package tech.raxit.gatekeeper.management.dto.response;

import tech.raxit.gatekeeper.management.entity.RouteConfigurationVersion;

import java.time.LocalDateTime;

public record RouteVersionResponse(
        Long id,
        String routeId,
        Long versionNumber,
        String changeType,
        String configurationSnapshot,
        LocalDateTime createdAt
) {

    public static RouteVersionResponse from(RouteConfigurationVersion version) {
        return new RouteVersionResponse(
                version.getId(),
                version.getRouteId(),
                version.getVersionNumber(),
                version.getChangeType(),
                version.getConfigurationSnapshot(),
                version.getCreatedAt()
        );
    }
}
