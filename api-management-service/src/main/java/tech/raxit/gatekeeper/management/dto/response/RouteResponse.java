package tech.raxit.gatekeeper.management.dto.response;

import tech.raxit.gatekeeper.management.entity.ApiRoute;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record RouteResponse(
        Long id,
        Long organizationId,
        String routeId,
        String name,
        String pathPattern,
        String targetBaseUrl,
        String targetPathPrefix,
        Integer requestsPerMinute,
        Integer responseTimeoutMs,
        boolean idempotencyEnabled,
        boolean active,
        Long configurationVersion,
        Set<String> allowedMethods,
        List<HeaderRuleResponse> headerRules,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static RouteResponse from(ApiRoute route) {
        List<HeaderRuleResponse> rules = route.getHeaderRules()
                .stream()
                .map(HeaderRuleResponse::from)
                .toList();

        return new RouteResponse(
                route.getId(),
                route.getOrganization().getId(),
                route.getRouteId(),
                route.getName(),
                route.getPathPattern(),
                route.getTargetBaseUrl(),
                route.getTargetPathPrefix(),
                route.getRequestsPerMinute(),
                route.getResponseTimeoutMs(),
                route.isIdempotencyEnabled(),
                route.isActive(),
                route.getConfigurationVersion(),
                Set.copyOf(route.getAllowedMethods()),
                rules,
                route.getCreatedAt(),
                route.getUpdatedAt()
        );
    }
}