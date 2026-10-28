package tech.raxit.gatekeeper.management.dto.request;

import jakarta.validation.constraints.NotNull;
import tech.raxit.gatekeeper.management.entity.enums.OrganizationStatus;

public record OrganizationStatusRequest(
        @NotNull(message = "Organization status is required")
        OrganizationStatus status
) {
}
