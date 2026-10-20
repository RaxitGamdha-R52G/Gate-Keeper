package tech.raxit.gatekeeper.management.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.raxit.gatekeeper.management.dto.request.organization.InitialUserDetails;
import tech.raxit.gatekeeper.management.dto.request.organization.OrganizationDetails;
import tech.raxit.gatekeeper.management.dto.request.organization.OrganizationRegistrationRequest;
import tech.raxit.gatekeeper.management.dto.response.organization.OrganizationRegistrationResponse;
import tech.raxit.gatekeeper.management.dto.response.organization.OrganizationResponse;
import tech.raxit.gatekeeper.management.dto.response.setup.IssuedSetupToken;
import tech.raxit.gatekeeper.management.entity.*;
import tech.raxit.gatekeeper.management.entity.enums.OrganizationStatus;
import tech.raxit.gatekeeper.management.event.organization.OrganizationStatusChangedEvent;
import tech.raxit.gatekeeper.management.event.setup.SetupInvitationRequested;
import tech.raxit.gatekeeper.management.exception.DuplicateResourceException;
import tech.raxit.gatekeeper.management.exception.OrganizationNotFoundException;
import tech.raxit.gatekeeper.management.mapper.OrganizationMapper;
import tech.raxit.gatekeeper.management.repository.*;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class OrganizationService {

    private static final String ORGANIZATION_ADMIN_ROLE =
            "ORGANIZATION_ADMIN";

    private final OrganizationRepository organizationRepository;
    private final ManagementUserRepository managementUserRepository;
    private final RoleRepository roleRepository;
    private final ControlPlaneAuditRecordRepository auditRecordRepository;
    private final OutboxEventRepository outboxEventRepository;

    private final ObjectMapper objectMapper;
    private final SetupTokenService setupTokenService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final OrganizationMapper organizationMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public OrganizationRegistrationResponse registerOrganization(
            OrganizationRegistrationRequest request
    ) {
        OrganizationDetails organizationDetails = request.organization();
        InitialUserDetails initialUserDetails = request.initialUser();

        validateRegistrationUniqueness(organizationDetails, initialUserDetails);

        Role adminRole = roleRepository
                .findByName(ORGANIZATION_ADMIN_ROLE)
                .orElseThrow(() -> new IllegalStateException(
                        "Required ORGANIZATION_ADMIN role is missing"
                ));

        LocalDateTime now = LocalDateTime.now();

        Organization organization = organizationMapper.toOrganizationEntity(organizationDetails);

        organization.setStatus(OrganizationStatus.INACTIVE);
        organization.setCreatedAt(now);
        organization.setUpdatedAt(now);

        organization = organizationRepository.save(organization);

        ManagementUser initialAdmin = organizationMapper.toManagementUserEntity(initialUserDetails);

        initialAdmin.setOrganization(organization);
        initialAdmin.setRole(adminRole);
        initialAdmin.setEnabled(false);

        String temporarySecret = UUID.randomUUID().toString();
        initialAdmin.setPasswordHash(
                passwordEncoder.encode(temporarySecret)
        );

        initialAdmin.setCreatedAt(now);
        initialAdmin.setUpdatedAt(now);

        initialAdmin = managementUserRepository.save(initialAdmin);

        IssuedSetupToken issuedToken = setupTokenService.issueToken(initialAdmin);

        publishEvent(organization, initialAdmin, issuedToken);

        return organizationMapper.toRegistrationResponse(organization, initialAdmin);
    }

    private void publishEvent(Organization organization, ManagementUser initialAdmin, IssuedSetupToken issuedToken) {
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
    }

    @Transactional(readOnly = true)
    public Page<OrganizationResponse> getOrganizations(
            Pageable pageable
    ) {
        return organizationRepository
                .findAllByDeletedAtIsNull(pageable)
                .map(organizationMapper::toResponseDto);
    }

    @Transactional(readOnly = true)
    public OrganizationResponse getOrganizationById(
            Long organizationId
    ) {
        return organizationMapper.toResponseDto(
                getOrganization(organizationId)
        );
    }

    @Transactional
    public OrganizationResponse updateStatus(
            Long organizationId,
            OrganizationStatus targetStatus
    ) {
        Organization organization = getOrganization(organizationId);
        OrganizationStatus currentStatus = organization.getStatus();

        if (currentStatus == targetStatus) {
            throw new DuplicateResourceException(
                    "Organization already has status " + targetStatus
            );
        }

        boolean validTransition = switch (currentStatus) {
            case INACTIVE -> targetStatus == OrganizationStatus.ACTIVE;
            case ACTIVE -> targetStatus == OrganizationStatus.INACTIVE
                    || targetStatus == OrganizationStatus.SUSPENDED;
            case SUSPENDED -> targetStatus == OrganizationStatus.ACTIVE
                    || targetStatus == OrganizationStatus.INACTIVE;
        };

        if (!validTransition) {
            throw new IllegalArgumentException(
                    "Invalid organization status transition from "
                            + currentStatus + " to " + targetStatus
            );
        }

        organization.setStatus(targetStatus);
        organization.setUpdatedAt(LocalDateTime.now());

        recordStatusChange(organization, currentStatus, targetStatus);
        enqueueStatusChangedEvent(organization, currentStatus, targetStatus);

        return organizationMapper.toResponseDto(organization);
    }

    private Organization getOrganization(Long organizationId) {
        return organizationRepository
                .findByIdAndDeletedAtIsNull(organizationId)
                .orElseThrow(() ->
                        new OrganizationNotFoundException(organizationId)
                );
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

    private void recordStatusChange(
            Organization organization,
            OrganizationStatus previousStatus,
            OrganizationStatus newStatus
    ) {
        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String actor = authentication != null
                ? authentication.getName()
                : "system";

        ControlPlaneAuditRecord record = new ControlPlaneAuditRecord();
        record.setOrganization(organization);
        record.setActor(actor);
        record.setAction("ORGANIZATION_STATUS_CHANGED");
        record.setResourceType("ORGANIZATION");
        record.setResourceId(organization.getId().toString());
        record.setDurationMs(0L);
        record.setResult("SUCCESS");
        record.setDetails(
                "Status changed from " + previousStatus + " to " + newStatus
        );
        record.setCreatedAt(LocalDateTime.now());

        auditRecordRepository.save(record);
    }

    private void enqueueStatusChangedEvent(
            Organization organization,
            OrganizationStatus previousStatus,
            OrganizationStatus newStatus
    ) {
        try {
            String payload = objectMapper.writeValueAsString(
                    new OrganizationStatusChangedEvent(
                            organization.getId(),
                            previousStatus,
                            newStatus,
                            organization.getUpdatedAt()
                    )
            );

            OutboxEvent event = new OutboxEvent();
            event.setAggregateType("ORGANIZATION");
            event.setAggregateId(organization.getId().toString());
            event.setEventType("ORGANIZATION_STATUS_CHANGED");
            event.setPayload(payload);
            event.setCreatedAt(LocalDateTime.now());
            event.setAttempts(0);

            outboxEventRepository.save(event);
        } catch (JacksonException exception) {
            throw new IllegalStateException(
                    "Could not serialize organization status event",
                    exception
            );
        }
    }
}
