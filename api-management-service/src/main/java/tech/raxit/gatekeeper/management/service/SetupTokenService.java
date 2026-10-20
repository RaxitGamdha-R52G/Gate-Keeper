package tech.raxit.gatekeeper.management.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.raxit.gatekeeper.management.dto.response.setup.IssuedSetupToken;
import tech.raxit.gatekeeper.management.entity.ManagementUser;
import tech.raxit.gatekeeper.management.entity.ManagementUserSetupToken;
import tech.raxit.gatekeeper.management.repository.ManagementUserSetupTokenRepository;
import tech.raxit.gatekeeper.management.util.SetupTokenGenerator;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SetupTokenService {

    @Value("${gatekeeper.setup-token.validity:30}")
    private long TOKEN_VALIDITY_MINUTES;

    private final ManagementUserSetupTokenRepository tokenRepository;
    private final SetupTokenGenerator tokenGenerator;

    @Transactional
    public IssuedSetupToken issueToken (ManagementUser user) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException(
                    "A persisted management user is required"
            );
        }

        if (user.isEnabled()) {
            throw new IllegalStateException(
                    "Cannot issue an initial setup token to an enabled user"
            );
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusMinutes(TOKEN_VALIDITY_MINUTES);

        List<ManagementUserSetupToken> pendingTokens =
                tokenRepository.findPendingTokensByUserId(user.getId());

        for (ManagementUserSetupToken token : pendingTokens) {
            token.revoke(now);
        }

        String rawToken = tokenGenerator.generateToken();
        String tokenHash = tokenGenerator.hashToken(rawToken);

        ManagementUserSetupToken setupToken =
                new ManagementUserSetupToken(
                        user,
                        tokenHash,
                        expiresAt,
                        now
                );

        tokenRepository.save(setupToken);

        return new IssuedSetupToken(rawToken, expiresAt);
    }
}
