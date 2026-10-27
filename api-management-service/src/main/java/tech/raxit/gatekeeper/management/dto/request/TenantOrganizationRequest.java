package tech.raxit.gatekeeper.management.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TenantOrganizationRequest(
        @NotBlank(message = "Organization name required")
        @Size(max = 150, message = "Organization name must not exceed 150 characters")
        String name
) {
}
