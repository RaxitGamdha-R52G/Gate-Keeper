package tech.raxit.gatekeeper.management.dto.request.organization;

import jakarta.validation.constraints.NotNull;
import tech.raxit.gatekeeper.management.entity.enums.OrganizationStatus;

public record UpdateOrganizationStatusRequest(
        @NotNull
        OrganizationStatus status
) {
}
