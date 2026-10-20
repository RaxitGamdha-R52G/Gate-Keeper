package tech.raxit.gatekeeper.management.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.raxit.gatekeeper.management.dto.request.organization.InitialUserDetails;
import tech.raxit.gatekeeper.management.dto.request.organization.OrganizationDetails;
import tech.raxit.gatekeeper.management.dto.request.organization.OrganizationRegistrationRequest;
import tech.raxit.gatekeeper.management.dto.response.organization.OrganizationRegistrationResponse;
import tech.raxit.gatekeeper.management.dto.response.setup.IssuedSetupToken;
import tech.raxit.gatekeeper.management.entity.ManagementUser;
import tech.raxit.gatekeeper.management.entity.Organization;
import tech.raxit.gatekeeper.management.entity.Role;
import tech.raxit.gatekeeper.management.entity.enums.OrganizationStatus;
import tech.raxit.gatekeeper.management.event.setup.SetupInvitationRequested;
import tech.raxit.gatekeeper.management.exception.DuplicateResourceException;
import tech.raxit.gatekeeper.management.mapper.OrganizationRegistrationMapper;
import tech.raxit.gatekeeper.management.repository.ManagementUserRepository;
import tech.raxit.gatekeeper.management.repository.OrganizationRepository;
import tech.raxit.gatekeeper.management.repository.RoleRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@PreAuthorize("hasRole('SUPER_ADMIN')")
@RequiredArgsConstructor
public class OrganizationRegistrationService {

    private static final String ORGANIZATION_ADMIN_ROLE = "ORGANIZATION_ADMIN";

    private final OrganizationRepository organizationRepository;
    private final ManagementUserRepository managementUserRepository;
    private final RoleRepository roleRepository;
    private final SetupTokenService setupTokenService;

    private final ApplicationEventPublisher applicationEventPublisher;
    private final OrganizationRegistrationMapper organizationRegistrationMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public OrganizationRegistrationResponse registerOrganization(
            OrganizationRegistrationRequest request) {

        OrganizationDetails organizationDetails = request.organization();
        InitialUserDetails initialUserDetails = request.initialUser();

        validateRegistrationUniqueness(organizationDetails, initialUserDetails);

        Role adminRole = roleRepository
                .findByName(ORGANIZATION_ADMIN_ROLE)
                .orElseThrow(() ->
                        new IllegalStateException("Required ORGANIZATION_ADMIN role is missing")
                );

        LocalDateTime now = LocalDateTime.now();

        Organization organization = organizationRegistrationMapper.toOrganizationEntity(organizationDetails);

        organization.setStatus(OrganizationStatus.INACTIVE);
        organization.setCreatedAt(now);
        organization.setUpdatedAt(now);

        organization = organizationRepository.save(organization);

        ManagementUser initialAdmin = organizationRegistrationMapper.toManagementUserEntity(initialUserDetails);

        initialAdmin.setOrganization(organization);
        initialAdmin.setRole(adminRole);
        initialAdmin.setEnabled(false);

        String temporarySecret = UUID.randomUUID().toString();
        initialAdmin.setPasswordHash(passwordEncoder.encode(temporarySecret));

        initialAdmin.setCreatedAt(now);
        initialAdmin.setUpdatedAt(now);

        initialAdmin = managementUserRepository.save(initialAdmin);

        IssuedSetupToken issuedToken = setupTokenService.issueToken(initialAdmin);

        applicationEventPublisher.publishEvent(
                new SetupInvitationRequested(
                        organization.getId(),
                        organization.getName(),
                        initialAdmin.getId(),
                        initialAdmin.getName(),
                        initialAdmin.getEmail(),
                        issuedToken.rawToken(),
                        issuedToken.expiresAt()
                )
        );

        return organizationRegistrationMapper.toRegistrationResponse(organization, initialAdmin);
    }

    private void validateRegistrationUniqueness(
            OrganizationDetails organization,
            InitialUserDetails user
    ) {

        if (organizationRepository.existsBySlug(organization.slug())) {
            throw new DuplicateResourceException(
                    "Organization slug already exists"
            );
        }

        if (organizationRepository.existsByName(organization.name())) {
            throw new DuplicateResourceException(
                    "Organization name already exists"
            );
        }

        if (managementUserRepository.existsByUsername(user.username())) {
            throw new DuplicateResourceException(
                    "Username already exists"
            );
        }

        if (managementUserRepository.existsByEmail(user.email())) {
            throw new DuplicateResourceException(
                    "Email already exists"
            );
        }
    }
}
