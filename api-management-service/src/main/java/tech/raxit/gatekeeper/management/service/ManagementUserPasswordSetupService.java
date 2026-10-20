package tech.raxit.gatekeeper.management.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.raxit.gatekeeper.management.entity.ManagementUser;
import tech.raxit.gatekeeper.management.entity.ManagementUserSetupToken;
import tech.raxit.gatekeeper.management.entity.Organization;
import tech.raxit.gatekeeper.management.repository.ManagementUserSetupTokenRepository;
import tech.raxit.gatekeeper.management.util.SetupTokenGenerator;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ManagementUserPasswordSetupService {

    private final ManagementUserSetupTokenRepository tokenRepository;
    private final SetupTokenGenerator tokenGenerator;
    private final PasswordEncoder passwordEncoder;

    private static void validateInput(
            String rawToken,
            String newPassword
    ) {
        if (rawToken == null || rawToken.isBlank()) {
            throw invalidToken();
        }

        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException(
                    "A new password is required"
            );
        }
    }

    private static IllegalArgumentException invalidToken() {
        return new IllegalArgumentException(
                "Setup token is invalid, expired, or already used"
        );
    }

    @Transactional
    public void completeSetup(String rawToken, String newPassword) {
        validateInput(rawToken, newPassword);

        String tokenHash = tokenGenerator.hashToken(rawToken);

        ManagementUserSetupToken setupToken = tokenRepository
                .findByTokenHashForUpdate(tokenHash)
                .orElseThrow(ManagementUserPasswordSetupService::invalidToken);

        LocalDateTime now = LocalDateTime.now();

        if (!setupToken.isUsableAt(now)) {
            throw invalidToken();
        }

        ManagementUser user = setupToken.getManagementUser();

        Organization organization = user.getOrganization();

        if (organization == null) {
            throw new IllegalStateException(
                    "Setup token is not associated with an organization user"
            );
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setEnabled(true);
        user.setUpdatedAt(now);

        setupToken.markUsed(now);
    }
}
