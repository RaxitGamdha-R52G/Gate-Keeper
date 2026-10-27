package tech.raxit.gatekeeper.management.dto.response;

import tech.raxit.gatekeeper.management.entity.Organization;
import tech.raxit.gatekeeper.management.entity.enums.OrganizationStatus;

import java.time.LocalDateTime;

public record OrganizationResponse(
        Long id,
        String name,
        String slug,
        OrganizationStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static OrganizationResponse from(Organization organization) {
        return new OrganizationResponse(
                organization.getId(),
                organization.getName(),
                organization.getSlug(),
                organization.getStatus(),
                organization.getCreatedAt(),
                organization.getUpdatedAt()
        );
    }
}
