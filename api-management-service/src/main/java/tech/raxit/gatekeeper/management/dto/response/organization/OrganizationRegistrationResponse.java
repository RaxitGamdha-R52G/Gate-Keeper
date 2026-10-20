package tech.raxit.gatekeeper.management.dto.response.organization;

import java.time.LocalDateTime;

public record OrganizationRegistrationResponse(
        Long id,
        String name,
        String slug,
        String status,
        LocalDateTime createdAt,
        RegisteredUserSummary initialUser
) {}