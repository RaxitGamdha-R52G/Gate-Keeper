package tech.raxit.gatekeeper.management.event.setup;

import java.time.LocalDateTime;
import java.util.Objects;

public record SetupInvitationRequested(
        Long organizationId,
        String organizationName,
        Long managementUserId,
        String recipientName,
        String recipientEmail,
        String rawToken,
        LocalDateTime expiresAt
) {
    public SetupInvitationRequested {
        Objects.requireNonNull(organizationId, "Organization ID is required");
        Objects.requireNonNull(organizationName, "Organization name is required");
        Objects.requireNonNull(managementUserId, "Management user ID is required");
        Objects.requireNonNull(recipientName, "Recipient name is required");
        Objects.requireNonNull(recipientEmail, "Recipient email is required");
        Objects.requireNonNull(rawToken, "Setup token is required");
        Objects.requireNonNull(expiresAt, "Token expiry is required");
    }

    @Override
    public String toString() {
        return "SetupInvitationRequested[" +
                "organizationId=" + organizationId +
                ", managementUserId=" + managementUserId +
                ", recipientEmail=[REDACTED]" +
                ", rawToken=[REDACTED]" +
                ", expiresAt=" + expiresAt +
                ']';
    }
}
