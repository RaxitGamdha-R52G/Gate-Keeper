package tech.raxit.gatekeeper.management.mapper;

import lombok.NonNull;
import org.springframework.stereotype.Component;
import tech.raxit.gatekeeper.management.dto.request.organization.InitialUserDetails;
import tech.raxit.gatekeeper.management.dto.request.organization.OrganizationDetails;
import tech.raxit.gatekeeper.management.dto.response.organization.OrganizationRegistrationResponse;
import tech.raxit.gatekeeper.management.dto.response.organization.RegisteredUserSummary;
import tech.raxit.gatekeeper.management.entity.ManagementUser;
import tech.raxit.gatekeeper.management.entity.Organization;

@Component
public class OrganizationRegistrationMapper {

    public Organization toOrganizationEntity(
            @NonNull OrganizationDetails details) {

        Organization organization = new Organization();
        organization.setName(details.name());
        organization.setSlug(details.slug());

        return organization;
    }

    public ManagementUser toManagementUserEntity(
            @NonNull InitialUserDetails details) {

        ManagementUser user = new ManagementUser();
        user.setName(details.name());
        user.setUsername(details.username());
        user.setEmail(details.email());

        return user;
    }

    public OrganizationRegistrationResponse toRegistrationResponse(
            @NonNull Organization organization,
            @NonNull ManagementUser initialUser) {

        RegisteredUserSummary userSummary =
                new RegisteredUserSummary(
                        initialUser.getId(),
                        initialUser.getName(),
                        initialUser.getUsername(),
                        initialUser.getEmail(),
                        initialUser.isEnabled()
                );

        return new OrganizationRegistrationResponse(
                organization.getId(),
                organization.getName(),
                organization.getSlug(),
                organization.getStatus().name(),
                organization.getCreatedAt(),
                userSummary
        );
    }
}