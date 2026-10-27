package tech.raxit.gatekeeper.management.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tech.raxit.gatekeeper.management.dto.request.TenantOrganizationRequest;
import tech.raxit.gatekeeper.management.dto.response.OrganizationResponse;
import tech.raxit.gatekeeper.management.service.OrganizationService;

@RestController
@RequestMapping("/api/tenant/organization")
@RequiredArgsConstructor
public class TenantOrganizationController {

    private final OrganizationService organizationService;

    @GetMapping
    public OrganizationResponse getOwnOrganization() {
        return organizationService.getOwnOrganization();
    }

    @PutMapping
    public OrganizationResponse updateOwnOrganization(
            @Valid @RequestBody TenantOrganizationRequest request) {
        return organizationService.updateOwnOrganization(request);
    }
}
