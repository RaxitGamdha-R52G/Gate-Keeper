package tech.raxit.gatekeeper.management.dto.response.setup;

import java.time.LocalDateTime;

public record IssuedSetupToken(
        String rawToken,
        LocalDateTime expiresAt
) {
}