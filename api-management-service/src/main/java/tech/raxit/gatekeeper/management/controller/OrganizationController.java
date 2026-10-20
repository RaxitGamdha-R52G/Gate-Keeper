package tech.raxit.gatekeeper.management.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tech.raxit.gatekeeper.management.dto.request.organization.OrganizationRegistrationRequest;
import tech.raxit.gatekeeper.management.dto.request.organization.UpdateOrganizationStatusRequest;
import tech.raxit.gatekeeper.management.dto.response.organization.OrganizationRegistrationResponse;
import tech.raxit.gatekeeper.management.dto.response.organization.OrganizationResponse;
import tech.raxit.gatekeeper.management.service.OrganizationService;

@RestController
@RequestMapping("/api/v1/organizations")
@PreAuthorize("hasRole('SUPER_ADMIN')")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;

    @PostMapping
    public ResponseEntity<OrganizationRegistrationResponse> registerOrganization(
            @Valid @RequestBody OrganizationRegistrationRequest request
    ) {
        OrganizationRegistrationResponse response =
                organizationService.registerOrganization(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public Page<OrganizationResponse> getOrganizations(
            @PageableDefault(
                    size = 20,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        return organizationService.getOrganizations(pageable);
    }

    @GetMapping("/{organizationId}")
    public OrganizationResponse getOrganization(
            @PathVariable Long organizationId
    ) {
        return organizationService.getOrganizationById(organizationId);
    }

    @PatchMapping("/{organizationId}")
    public OrganizationResponse updateOrganizationStatus(
            @PathVariable Long organizationId,
            @Valid @RequestBody UpdateOrganizationStatusRequest request
    ) {
        return organizationService.updateStatus(
                organizationId,
                request.status()
        );
    }
}
