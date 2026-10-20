package tech.raxit.gatekeeper.management.dto.response.organization;

public record RegisteredUserSummary(
        Long id,
        String name,
        String username,
        String email,
        boolean enabled
) {}
