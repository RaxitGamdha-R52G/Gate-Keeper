package tech.raxit.gatekeeper.management.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.raxit.gatekeeper.management.dto.request.OrganizationRequest;
import tech.raxit.gatekeeper.management.dto.request.OrganizationStatusRequest;
import tech.raxit.gatekeeper.management.dto.request.TenantAdminRequest;
import tech.raxit.gatekeeper.management.dto.request.TenantOrganizationRequest;
import tech.raxit.gatekeeper.management.dto.response.OrganizationResponse;
import tech.raxit.gatekeeper.management.dto.response.TenantAdminResponse;
import tech.raxit.gatekeeper.management.entity.ManagementUser;
import tech.raxit.gatekeeper.management.entity.Organization;
import tech.raxit.gatekeeper.management.entity.Role;
import tech.raxit.gatekeeper.management.entity.enums.OrganizationStatus;
import tech.raxit.gatekeeper.management.exception.ApiException;
import tech.raxit.gatekeeper.management.repository.ManagementUserRepository;
import tech.raxit.gatekeeper.management.repository.OrganizationRepository;
import tech.raxit.gatekeeper.management.repository.RoleRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final ManagementUserRepository managementUserRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final TenantContext tenantContext;
    private final AuditService auditService;

    @Transactional
    public OrganizationResponse registerOrganization(OrganizationRequest request) {
        long startedAt = System.nanoTime();

        checkUnique(request.name(), request.slug(), null);

        Organization organization = new Organization();
        organization.setName(request.name());
        organization.setSlug(request.slug());

        organizationRepository.save(organization);

        auditService.record(organization, "CREATE_ORGANIZATION", "ORGANIZATION",
                organization.getId().toString(), startedAt,
                "Organization created in PENDING status");

        return OrganizationResponse.from(organization);
    }

    @Transactional(readOnly = true)
    public Page<OrganizationResponse> getOrganizations(Pageable pageable) {
        return organizationRepository
                .findAllByStatusNot(OrganizationStatus.DELETED, pageable)
                .map(OrganizationResponse::from);
    }

    @Transactional(readOnly = true)
    public OrganizationResponse getOrganizationById(Long id) {
        return OrganizationResponse.from(getOrganization(id));

    }

    private Organization getOrganization(Long id) {
        return organizationRepository
                .findByIdAndStatusNot(id, OrganizationStatus.DELETED)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        "Organization not found"
                ));
    }

    private void checkUnique(String name, String slug, Long currentId) {
        boolean nameExists = currentId == null
                ? organizationRepository.existsByNameIgnoreCase(name)
                : organizationRepository.existsByNameIgnoreCaseAndIdNot(name, currentId);

        if (nameExists) {
            throw new ApiException(
                    HttpStatus.CONFLICT,
                    "Organization name already exists"
            );
        }

        boolean slugExists = currentId == null
                ? organizationRepository.existsBySlugIgnoreCase(slug)
                : organizationRepository.existsBySlugIgnoreCaseAndIdNot(slug, currentId);

        if (slugExists) {
            throw new ApiException(
                    HttpStatus.CONFLICT,
                    "Organization slug already exists"
            );
        }
    }

    @Transactional
    public OrganizationResponse updateOrganization(Long id, OrganizationRequest request) {
        long startedAt = System.nanoTime();

        checkUnique(request.name(), request.slug(), id);

        Organization organizationToUpdate = getOrganization(id);

        organizationToUpdate.setName(request.name());
        organizationToUpdate.setSlug(request.slug());

        organizationRepository.flush();

        auditService.record(organizationToUpdate, "UPDATE_ORGANIZATION",
                "ORGANIZATION", id.toString(), startedAt,
                "Organization name and slug updated");

        return OrganizationResponse.from(organizationToUpdate);
    }

    @Transactional
    public OrganizationResponse changeStatus(Long id, OrganizationStatusRequest request) {
        long startedAt = System.nanoTime();

        Organization organization = getOrganization(id);

        if (request.status() == OrganizationStatus.DELETED) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "Use DELETE to soft-delete an organization"
            );
        }

        boolean allowed = switch (organization.getStatus()) {
            case ACTIVE -> request.status() == OrganizationStatus.INACTIVE
                    || request.status() == OrganizationStatus.SUSPENDED;

            case INACTIVE -> request.status() == OrganizationStatus.ACTIVE
                    || request.status() == OrganizationStatus.SUSPENDED;

            case PENDING, SUSPENDED -> request.status() == OrganizationStatus.ACTIVE
                    || request.status() == OrganizationStatus.INACTIVE;

            case DELETED -> false;
        };

        if (!allowed) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid organization status change request"
            );
        }

        organization.setStatus(request.status());

        organizationRepository.flush();

        auditService.record(organization, "CHANGE_ORGANIZATION_STATUS",
                "ORGANIZATION", id.toString(),
                startedAt, "Status changed to " + request.status());

        return OrganizationResponse.from(organization);
    }

    @Transactional
    public void delete(Long id) {
        long startedAt = System.nanoTime();

        Organization organization = getOrganization(id);

        organization.setStatus(OrganizationStatus.DELETED);
        organization.setDeletedAt(LocalDateTime.now());

        auditService.record(organization, "DELETE_ORGANIZATION",
                "ORGANIZATION", id.toString(), startedAt,
                "Organization soft-deleted");
    }

    @Transactional
    public TenantAdminResponse createTenantAdmin(Long organizationId, TenantAdminRequest request) {
        long startedAt = System.nanoTime();

        Organization organization = getOrganization(organizationId);

        checkTenantAdminExists(organizationId, request);

        Role role = getTenantAdminRole();

        ManagementUser user = new ManagementUser();

        user.setOrganization(organization);
        user.setRole(role);
        user.setUsername(request.username());
        user.setPasswordHash(passwordEncoder.encode(request.password()));

        managementUserRepository.save(user);

        auditService.record(organization, "CREATE_TENANT_ADMIN",
                "MANAGEMENT_USER", user.getId().toString(),
                startedAt, "Tenant administrator created");

        return TenantAdminResponse.from(user);
    }

    private void checkTenantAdminExists(Long organizationId, TenantAdminRequest request) {
        if (managementUserRepository.existsByOrganizationId(organizationId)) {
            throw new ApiException(
                    HttpStatus.CONFLICT,
                    "This organization already has an administrator"
            );
        }

        if (managementUserRepository.existsByUsername(request.username())) {
            throw new ApiException(
                    HttpStatus.CONFLICT,
                    "Username already exists"
            );
        }
    }

    private Role getTenantAdminRole() {
        return roleRepository
                .findByName("ORGANIZATION_ADMIN")
                .orElseThrow(() -> new ApiException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "ORGANIZATION_ADMIN role is missing"
                ));
    }

    @Transactional(readOnly = true)
    public TenantAdminResponse getTenantAdminByOrganizationId(Long organizationId) {
        return TenantAdminResponse.from(getTenantAdmin(organizationId));
    }

    @Transactional
    public TenantAdminResponse updateTenantAdmin(Long organizationId, TenantAdminRequest request) {
        long startedAt = System.nanoTime();

        ManagementUser user = getTenantAdmin(organizationId);

        if (!user.getUsername().equals(request.username())
                && managementUserRepository.existsByUsername(request.username())) {
            throw new ApiException(HttpStatus.CONFLICT, "Username already exists");
        }

        user.setUsername(request.username());
        user.setPasswordHash(passwordEncoder.encode(request.password()));

        managementUserRepository.flush();

        auditService.record(user.getOrganization(), "UPDATE_TENANT_ADMIN",
                "MANAGEMENT_USER", user.getId().toString(), startedAt,
                "Tenant administrator updated");

        return TenantAdminResponse.from(user);
    }

    @Transactional
    public void deleteTenantAdmin(Long organizationId) {
        long startedAt = System.nanoTime();

        ManagementUser user = getTenantAdmin(organizationId);

        managementUserRepository.delete(user);

        auditService.record(user.getOrganization(), "DELETE_TENANT_ADMIN",
                "MANAGEMENT_USER", user.getId().toString(), startedAt,
                "Tenant administrator deleted");
    }

    private ManagementUser getTenantAdmin(Long organizationId) {
        getOrganization(organizationId);

        return managementUserRepository
                .findByOrganizationId(organizationId)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        "Organization administrator not found"
                ));
    }

    @Transactional(readOnly = true)
    public OrganizationResponse getOwnOrganization() {
        return OrganizationResponse.from(tenantContext.getOrganization());
    }

    @Transactional
    public OrganizationResponse updateOwnOrganization(TenantOrganizationRequest request) {
        long startedAt = System.nanoTime();

        Organization organization = tenantContext.getOrganization();

        if (!organization.getName().equalsIgnoreCase(request.name())
                && organizationRepository.existsByNameIgnoreCase(request.name())) {
            throw new ApiException(
                    HttpStatus.CONFLICT,
                    "Organization name already exists"
            );
        }

        organization.setName(request.name());

        organizationRepository.flush();

        auditService.record(organization, "UPDATE_OWN_ORGANIZATION",
                "ORGANIZATION", organization.getId().toString(), startedAt,
                "Tenant updated its organization name");

        return OrganizationResponse.from(organization);
    }
}
