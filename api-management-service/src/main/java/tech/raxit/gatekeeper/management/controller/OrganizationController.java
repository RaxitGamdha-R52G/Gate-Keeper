package tech.raxit.gatekeeper.management.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.raxit.gatekeeper.management.dto.request.organization.OrganizationRegistrationRequest;
import tech.raxit.gatekeeper.management.dto.response.organization.OrganizationRegistrationResponse;
import tech.raxit.gatekeeper.management.service.OrganizationRegistrationService;

@RestController
@RequestMapping("/api/v1/organizations")
@RequiredArgsConstructor
public class OrganizationController {
    private final OrganizationRegistrationService organizationRegistrationService;

    @PostMapping
    public ResponseEntity<OrganizationRegistrationResponse> registerOrganization(
            @Valid @RequestBody OrganizationRegistrationRequest registrationRequest
    ) {
        OrganizationRegistrationResponse response =
                organizationRegistrationService.registerOrganization(registrationRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
