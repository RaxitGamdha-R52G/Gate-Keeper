package tech.raxit.gatekeeper.management.dto.response;

public record LoginResponse(
        String token,
        String tokenType,
        long expiresInSeconds,
        String role,
        Long organizationId
) {
}
