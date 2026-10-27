package tech.raxit.gatekeeper.management.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tech.raxit.gatekeeper.management.entity.Organization;
import tech.raxit.gatekeeper.management.entity.enums.OrganizationStatus;
import tech.raxit.gatekeeper.management.exception.ApiException;
import tech.raxit.gatekeeper.management.repository.OrganizationRepository;

@Component
@RequiredArgsConstructor
public class TenantContext {

    private final OrganizationRepository organizationRepository;

    @Transactional
    public Organization getOrganization() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Long organizationId = null;

        if(authentication instanceof JwtAuthenticationToken jwtAuthenticationToken) {
            organizationId = jwtAuthenticationToken.getToken().getClaim("organizationId");
        }

        if(organizationId == null) {
            invalidateToken();
        }

        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new ApiException(HttpStatus.FORBIDDEN, "Organization does not exist"));

        boolean isActive = switch (organization.getStatus()) {
            case ACTIVE, INACTIVE -> true;
            default -> false;
        };

        if(!isActive) {
            throw new ApiException(
                    HttpStatus.FORBIDDEN,
                    "The organization is not active"
            );
        }

        return organization;
    }

    private static void invalidateToken() {
        throw new ApiException(
                HttpStatus.FORBIDDEN,
                "Tenant identity is missing"
        );
    }
}
