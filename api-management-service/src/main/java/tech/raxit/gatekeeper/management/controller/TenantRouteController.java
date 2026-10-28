package tech.raxit.gatekeeper.management.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tech.raxit.gatekeeper.management.dto.request.RouteRequest;
import tech.raxit.gatekeeper.management.dto.request.RouteStatusRequest;
import tech.raxit.gatekeeper.management.dto.response.AuditRecordResponse;
import tech.raxit.gatekeeper.management.dto.response.RouteResponse;
import tech.raxit.gatekeeper.management.dto.response.RouteVersionResponse;
import tech.raxit.gatekeeper.management.service.RouteService;

import java.util.List;

@RestController
@RequestMapping("/api/tenant")
@RequiredArgsConstructor
public class TenantRouteController {

    private final RouteService routeService;

    @PostMapping("/routes")
    @ResponseStatus(HttpStatus.CREATED)
    public RouteResponse create(
            @Valid @RequestBody RouteRequest request
    ) {
        return routeService.create(request);
    }

    @GetMapping("/routes")
    public List<RouteResponse> findAll() {
        return routeService.findAllForTenant();
    }

    @GetMapping("/routes/{id}")
    public RouteResponse findOne(@PathVariable Long id) {
        return routeService.findById(id);
    }

    @PutMapping("/routes/{id}")
    public RouteResponse update(
            @PathVariable Long id,
            @Valid @RequestBody RouteRequest request
    ) {
        return routeService.update(id, request);
    }

    @PatchMapping("/routes/{id}/status")
    public RouteResponse changeStatus(
            @PathVariable Long id,
            @Valid @RequestBody RouteStatusRequest request
    ) {
        return routeService.changeStatus(id, request);
    }

    @DeleteMapping("/routes/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        routeService.delete(id);
    }

    @GetMapping("/route-versions/{routeId}")
    public List<RouteVersionResponse> getVersions(
            @PathVariable String routeId) {
        return routeService.getVersions(routeId);
    }

    @GetMapping("/audit-records")
    public List<AuditRecordResponse> getAuditRecords() {
        return routeService.getAuditRecords();
    }
}
