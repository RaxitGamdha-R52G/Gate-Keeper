package tech.raxit.gatekeeper.management.event.organization;

import tech.raxit.gatekeeper.management.entity.enums.OrganizationStatus;

import java.time.LocalDateTime;

public record OrganizationStatusChangedEvent(
        Long organizationId,
        OrganizationStatus previousStatus,
        OrganizationStatus newStatus,
        LocalDateTime changedAt
) {
}