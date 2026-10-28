package tech.raxit.gatekeeper.management.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.raxit.gatekeeper.management.dto.request.HeaderRuleRequest;
import tech.raxit.gatekeeper.management.dto.request.RouteRequest;
import tech.raxit.gatekeeper.management.dto.request.RouteStatusRequest;
import tech.raxit.gatekeeper.management.dto.response.AuditRecordResponse;
import tech.raxit.gatekeeper.management.dto.response.RouteResponse;
import tech.raxit.gatekeeper.management.dto.response.RouteVersionResponse;
import tech.raxit.gatekeeper.management.entity.ApiRoute;
import tech.raxit.gatekeeper.management.entity.Organization;
import tech.raxit.gatekeeper.management.entity.RouteConfigurationVersion;
import tech.raxit.gatekeeper.management.entity.RouteHeaderRule;
import tech.raxit.gatekeeper.management.entity.enums.OrganizationStatus;
import tech.raxit.gatekeeper.management.exception.ApiException;
import tech.raxit.gatekeeper.management.repository.ApiRouteRepository;
import tech.raxit.gatekeeper.management.repository.ControlPlaneAuditRecordRepository;
import tech.raxit.gatekeeper.management.repository.RouteConfigurationVersionRepository;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RouteService {

    private final ApiRouteRepository apiRouteRepository;
    private final RouteConfigurationVersionRepository versionRepository;
    private final ControlPlaneAuditRecordRepository auditRecordRepository;
    private final TenantContext tenantContext;
    private final AuditService auditService;
    private final ObjectMapper objectMapper;

    @Transactional
    public RouteResponse create(RouteRequest request) {
        long startedAt = System.nanoTime();

        Organization organization = tenantContext.getOrganization();
        validate(request, organization);

        if (apiRouteRepository.existsByOrganizationIdAndRouteId(organization.getId(), request.routeId())) {
            throw new ApiException(
                    HttpStatus.CONFLICT,
                    "Route ID already exists for this organization"
            );
        }

        ApiRoute route = new ApiRoute();
        route.setOrganization(organization);
        route.setConfigurationVersion(1L);

        toEntity(route, request);

        apiRouteRepository.save(route);

        saveVersion(route, "CREATED");

        auditService.record(organization, "CREATE_ROUTE",
                "API_ROUTE", route.getRouteId(), startedAt,
                "Route configuration version 1 created");

        return RouteResponse.from(route);
    }

    @Transactional(readOnly = true)
    public List<RouteResponse> findAllForTenant() {
        Organization organization = tenantContext.getOrganization();

        return apiRouteRepository.findAllByOrganizationId(organization.getId())
                .stream()
                .map(RouteResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public RouteResponse findById(Long id) {
        Organization organization = tenantContext.getOrganization();
        return RouteResponse.from(getOwnedRoute(id, organization.getId()));
    }

    @Transactional
    public RouteResponse update(Long id, RouteRequest request) {
        long startedAt = System.nanoTime();

        Organization organization = tenantContext.getOrganization();
        ApiRoute route = getOwnedRoute(id, organization.getId());

        if (!route.getRouteId().equals(request.routeId())) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "routeId cannot be changed"
            );
        }

        validate(request, organization);

        route.setConfigurationVersion(route.getConfigurationVersion() + 1);

        toEntity(route, request);

        apiRouteRepository.flush();

        saveVersion(route, "UPDATED");

        auditService.record(organization, "UPDATE_ROUTE", "API_ROUTE",
                route.getRouteId(), startedAt, "Route configuration version %d created".formatted(route.getConfigurationVersion()));

        return RouteResponse.from(route);
    }

    @Transactional
    public RouteResponse changeStatus(Long id, RouteStatusRequest request) {
        long startedAt = System.nanoTime();

        Organization organization = tenantContext.getOrganization();
        ApiRoute route = getOwnedRoute(id, organization.getId());

        route.setActive(request.active());
        route.setConfigurationVersion(route.getConfigurationVersion() + 1);

        String changeType = request.active() ? "ACTIVATED" : "DEACTIVATED";
        String action = request.active() ? "ACTIVATE_ROUTE" : "DEACTIVATE_ROUTE";

        apiRouteRepository.flush();

        saveVersion(route, changeType);

        auditService.record(organization, action, "API_ROUTE", route.getRouteId(), startedAt,
                "Route configuration version %d created".formatted(route.getConfigurationVersion()));

        return RouteResponse.from(route);
    }

    @Transactional
    public void delete(Long id) {
        long startedAt = System.nanoTime();

        Organization organization = tenantContext.getOrganization();
        ApiRoute route = getOwnedRoute(id, organization.getId());
        route.setConfigurationVersion(route.getConfigurationVersion() + 1);

        saveVersion(route, "DELETED");

        auditService.record(organization, "DELETE_ROUTE", "API_ROUTE", route.getRouteId(), startedAt,
                "Final configuration saved as version %d".formatted(route.getConfigurationVersion()));

        apiRouteRepository.delete(route);
    }

    @Transactional(readOnly = true)
    public List<RouteVersionResponse> getVersions(String routeId) {
        Organization organization = tenantContext.getOrganization();

        return versionRepository
                .findAllByOrganizationIdAndRouteIdOrderByVersionNumberDesc(
                        organization.getId(), routeId
                )
                .stream()
                .map(RouteVersionResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RouteResponse> findAllActiveForGateway() {
        return apiRouteRepository
                .findAllByActiveTrueAndOrganizationStatus(OrganizationStatus.ACTIVE)
                .stream()
                .map(RouteResponse::from)
                .toList();
    }

    private ApiRoute getOwnedRoute(Long id, Long organizationId) {
        return apiRouteRepository
                .findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        "Route not found"
                ));
    }

    private void toEntity(ApiRoute route, RouteRequest request) {
        route.setRouteId(request.routeId());
        route.setName(request.name());
        route.setPathPattern(request.pathPattern());
        route.setTargetBaseUrl(request.targetBaseUrl());
        route.setTargetPathPrefix(request.targetPathPrefix());
        route.setRequestsPerMinute(request.requestsPerMinute());
        route.setResponseTimeoutMs(request.responseTimeoutMs());
        route.setIdempotencyEnabled(request.idempotencyEnabled());
        route.setActive(request.active());
        route.setAllowedMethods(normalizeMethods(request.allowedMethods()));

        route.getHeaderRules().clear();
        for (HeaderRuleRequest ruleRequest : request.headerRules()) {
            RouteHeaderRule rule = new RouteHeaderRule();

            rule.setRoute(route);
            rule.setDirection(ruleRequest.direction().toUpperCase(Locale.ENGLISH));
            rule.setAction(ruleRequest.action().toUpperCase(Locale.ENGLISH));
            rule.setHeaderName(ruleRequest.headerName());
            rule.setHeaderValue(ruleRequest.headerValue());
            route.getHeaderRules().add(rule);
        }
    }

    private void validate(RouteRequest request, Organization organization) {
        String tenantPrefix = "/" + organization.getSlug() + "/";

        if (!request.pathPattern().startsWith(tenantPrefix)) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "pathPattern must start with the tenant prefix " + tenantPrefix
            );
        }

        if (!request.targetPathPrefix().startsWith("/")) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "targetPathPrefix must start with /"
            );
        }

        try {
            URI uri = URI.create(request.targetBaseUrl());
            if (!"http".equalsIgnoreCase(uri.getScheme()) && !"https".equalsIgnoreCase(uri.getScheme())) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException exception) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "targetBaseUrl must be a valid HTTP or HTTPS URL");
        }

        normalizeMethods(request.allowedMethods());

        for (HeaderRuleRequest rule : request.headerRules()) {
            String direction = rule.direction().toUpperCase(Locale.ENGLISH);
            String action = rule.action().toUpperCase(Locale.ENGLISH);

            if (!"REQUEST".equals(direction) && !"RESPONSE".equals(direction)) {
                throw new ApiException(
                        HttpStatus.BAD_REQUEST,
                        "Header direction must be REQUEST or RESPONSE"
                );
            }
            if (!"ADD".equals(action) && !"REMOVE".equals(action)) {
                throw new ApiException(
                        HttpStatus.BAD_REQUEST,
                        "Header action must be ADD or REMOVE"
                );
            }
            if ("ADD".equals(action) && (rule.headerValue() == null || rule.headerValue().trim().isEmpty())) {
                throw new ApiException(
                        HttpStatus.BAD_REQUEST,
                        "ADD header rules require headerValue"
                );
            }
        }
    }

    private Set<String> normalizeMethods(Set<String> methods) {
        return methods
                .stream()
                .map(method -> method.toUpperCase(Locale.ROOT))
                .map(method -> {
                    try {
                        HttpMethod.valueOf(method);
                        return method;
                    } catch (IllegalArgumentException exception) {
                        throw new ApiException(
                                HttpStatus.BAD_REQUEST,
                                "Unsupported HTTP method: " + method
                        );
                    }
                })
                .collect(Collectors.toSet());
    }

    private void saveVersion(ApiRoute route, String changeType) {
        RouteConfigurationVersion version = new RouteConfigurationVersion();

        version.setOrganization(route.getOrganization());
        version.setRouteId(route.getRouteId());
        version.setVersionNumber(route.getConfigurationVersion());
        version.setChangeType(changeType);
        version.setConfigurationSnapshot(objectMapper.writeValueAsString(RouteResponse.from(route)));

        versionRepository.save(version);
    }


    @Transactional(readOnly = true)
    public List<AuditRecordResponse> getAuditRecords() {
        Organization organization = tenantContext.getOrganization();

        return auditRecordRepository
                .findAllByOrganizationIdOrderByCreatedAtDesc(organization.getId())
                .stream()
                .map(AuditRecordResponse::from)
                .toList();
    }
}