package tech.raxit.gatekeeper.management.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tech.raxit.gatekeeper.management.dto.request.OrganizationRequest;
import tech.raxit.gatekeeper.management.dto.request.OrganizationStatusRequest;
import tech.raxit.gatekeeper.management.dto.request.TenantAdminRequest;
import tech.raxit.gatekeeper.management.dto.response.OrganizationResponse;
import tech.raxit.gatekeeper.management.dto.response.TenantAdminResponse;
import tech.raxit.gatekeeper.management.service.OrganizationService;

@RestController
@RequestMapping("/api/platform/organizations")
@RequiredArgsConstructor
public class PlatformOrganizationController {
    private final OrganizationService organizationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrganizationResponse create(
            @Valid @RequestBody OrganizationRequest request
    ) {
        return organizationService.registerOrganization(request);
    }

    @GetMapping
    public Page<OrganizationResponse> getAll(
            @PageableDefault(
                    size = 15,
                    sort = "id",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        return organizationService.getOrganizations(pageable);
    }

    @GetMapping("/{id}")
    public OrganizationResponse findById(@PathVariable Long id) {
        return organizationService.getOrganizationById(id);
    }

    @PutMapping("/{id}")
    public OrganizationResponse update(
            @PathVariable Long id,
            @Valid @RequestBody OrganizationRequest request
    ) {
        return organizationService.updateOrganization(id, request);
    }

    @PatchMapping("/{id}/status")
    public OrganizationResponse changeStatus(
            @PathVariable Long id,
            @Valid @RequestBody OrganizationStatusRequest request
    ) {

        return organizationService.changeStatus(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        organizationService.delete(id);
    }
    

    @PostMapping("/{organizationId}/tenant-admin")
    @ResponseStatus(HttpStatus.CREATED)
    public TenantAdminResponse createTenantAdmin(
            @PathVariable Long organizationId,
            @Valid @RequestBody TenantAdminRequest request
    ) {
        return organizationService.createTenantAdmin(organizationId, request);
    }

    @GetMapping("/{organizationId}/tenant-admin")
    public TenantAdminResponse getTenantAdmin(@PathVariable Long organizationId) {
        return organizationService.getTenantAdminByOrganizationId(organizationId);
    }

    @PutMapping("/{organizationId}/tenant-admin")
    public TenantAdminResponse updateTenantAdmin(
            @PathVariable Long organizationId,
            @Valid @RequestBody TenantAdminRequest request
    ) {
        return organizationService.updateTenantAdmin(organizationId, request);
    }

    @DeleteMapping("/{organizationId}/tenant-admin")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTenantAdmin(@PathVariable Long organizationId) {
        organizationService.deleteTenantAdmin(organizationId);
    }
}
