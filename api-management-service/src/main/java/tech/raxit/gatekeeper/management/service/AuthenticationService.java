package tech.raxit.gatekeeper.management.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.raxit.gatekeeper.management.dto.request.LoginRequest;
import tech.raxit.gatekeeper.management.dto.response.LoginResponse;
import tech.raxit.gatekeeper.management.entity.ManagementUser;
import tech.raxit.gatekeeper.management.entity.enums.OrganizationStatus;
import tech.raxit.gatekeeper.management.exception.ApiException;
import tech.raxit.gatekeeper.management.repository.ManagementUserRepository;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final ManagementUserRepository managementUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;

    @Value("${security.jwt.expiration-seconds}")
    private long expirationSeconds;

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {

        ManagementUser user = managementUserRepository
                .findByUsername(request.username())
                .orElseThrow(this::invalidCredentials);

        if (!user.isEnabled() || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw invalidCredentials();
        }

        if (user.getOrganization() != null && user.getOrganization().getStatus() != OrganizationStatus.ACTIVE) {
            throw new ApiException(HttpStatus.FORBIDDEN, "The organization is not active");
        }

        Instant issuedAt = Instant.now();
        String role = user.getRole().getName();
        Long organizationId = user.getOrganization() == null ? null : user.getOrganization().getId();

        JwtClaimsSet.Builder claims = JwtClaimsSet.builder()
                .issuer("gatekeeper-api-management")
                .issuedAt(issuedAt)
                .expiresAt(issuedAt.plusSeconds(expirationSeconds))
                .subject(user.getUsername())
                .claim("role", role);

        if (organizationId != null) {
            claims.claim("organizationId", organizationId);
        }

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = jwtEncoder
                .encode(
                        JwtEncoderParameters.from(header, claims.build())
                ).getTokenValue();

        return new LoginResponse(
                token,
                "Bearer",
                expirationSeconds,
                role,
                organizationId
        );
    }

    private RuntimeException invalidCredentials() {
        return new ApiException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
    }
}
