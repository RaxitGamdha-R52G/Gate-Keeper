package tech.raxit.gatekeeper.management.dto.response.organization;

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
}
